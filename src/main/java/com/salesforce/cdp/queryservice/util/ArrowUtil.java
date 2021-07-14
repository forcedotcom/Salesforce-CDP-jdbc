package com.salesforce.cdp.queryservice.util;

import com.salesforce.cdp.queryservice.model.QueryServiceResponse;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.DecimalVector;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.util.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the utilities for processing the arrow stream.
 */
public class ArrowUtil {
	/**
	 * Converts the arrow stream in to List<Map<String,Object>> so that it can then be converted into result set format.
	 * @param queryServiceResponse Response received from query service
	 * @return List of data map.
	 * @throws SQLException
	 */
	public List<Map<String,Object>> getResultSetDataFromArrowStream(QueryServiceResponse queryServiceResponse) throws SQLException {

		byte[] bytes = Base64.getDecoder().decode(queryServiceResponse.getArrowStream());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		List<FieldVector> fieldVectors = null;
		RootAllocator allocator = new RootAllocator(Long.MAX_VALUE);
		List<Map<String, Object>> data = new ArrayList<>();
		try (ArrowStreamReader arrowStreamReader = new ArrowStreamReader(inputStream, allocator)) {

			VectorSchemaRoot root = arrowStreamReader.getVectorSchemaRoot();
			fieldVectors = root.getFieldVectors();
			while (arrowStreamReader.loadNextBatch()) {
				int rowCount = fieldVectors.get(0).getValueCount();
				for(int i=0;i<rowCount;++i) {
					Map<String,Object> row = new HashMap<>();
					for(FieldVector fieldVector : fieldVectors) {
						String fieldName = fieldVector.getField().getName();
						Object fieldValue = this.getFieldValue(fieldVector,i);
						row.put(fieldName,fieldValue);
					}
					data.add(row);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException("Failed to parse the arrow stream", e);
		}
		finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				inputStream = null;
			}
			if(fieldVectors != null){
				for(FieldVector fieldVector : fieldVectors) {
					if(fieldVector !=  null) {
						fieldVector.close();
					}
				}
				fieldVectors = null;
			}
			allocator.close();
			allocator =  null;
		}
		return  data;
	}

	private Object getFieldValue(FieldVector fieldVector, int index) throws SQLException {
		Types.MinorType type = Types.getMinorTypeForArrowType(fieldVector.getField().getType());

		if(fieldVector.isNull(index)) {
			return null;
		}

		if(type == Types.MinorType.VARCHAR) {
			Text value = ((VarCharVector)fieldVector).getObject(index);
			if(value != null)
				return value.toString();
			return null;
		}
		else if(type == Types.MinorType.INT) {
			return ((IntVector)fieldVector).get(index);
		}
		else if(type == Types.MinorType.DECIMAL) {
			return ((DecimalVector)fieldVector).getObject(index);
		}
		throw new SQLException("Unknown arrow type {}", type.name());
	}
}
