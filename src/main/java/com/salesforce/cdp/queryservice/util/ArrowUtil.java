package com.salesforce.cdp.queryservice.util;

import com.salesforce.cdp.queryservice.model.QueryServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.BitVector;
import org.apache.arrow.vector.DateDayVector;
import org.apache.arrow.vector.DecimalVector;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.Float4Vector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.SmallIntVector;
import org.apache.arrow.vector.TimeNanoVector;
import org.apache.arrow.vector.TimeStampMilliTZVector;
import org.apache.arrow.vector.TimeStampMilliVector;
import org.apache.arrow.vector.TimeStampNanoTZVector;
import org.apache.arrow.vector.TimeStampNanoVector;
import org.apache.arrow.vector.TinyIntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.util.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the utilities for processing the arrow stream.
 */
@Slf4j
public class ArrowUtil {
	/**
	 * Converts the arrow stream in to List<Map<String,Object>> so that it can then be converted into result set format.
	 * @param queryServiceResponse Response received from query service
	 * @return List of data map.
	 * @throws SQLException
	 */
	public List<Object> getResultSetDataFromArrowStream(QueryServiceResponse queryServiceResponse, boolean isCursorBasedPaginationReq) throws SQLException {

		byte[] bytes = Base64.getDecoder().decode(queryServiceResponse.getArrowStream());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		List<FieldVector> fieldVectors = null;
		RootAllocator allocator = new RootAllocator(Long.MAX_VALUE);
		List<Object> data = new ArrayList<>();
		try (ArrowStreamReader arrowStreamReader = new ArrowStreamReader(inputStream, allocator)) {

			VectorSchemaRoot root = arrowStreamReader.getVectorSchemaRoot();
			fieldVectors = root.getFieldVectors();
			while (arrowStreamReader.loadNextBatch()) {
				int rowCount = fieldVectors.get(0).getValueCount();
				for(int i=0;i<rowCount;++i) {

					if(isCursorBasedPaginationReq) {
						List<Object> row = new ArrayList<>();
						for(FieldVector fieldVector : fieldVectors) {
							Object fieldValue = this.getFieldValue(fieldVector,i);
							row.add(fieldValue);
						}
						data.add(row);
					} else {
						Map<String,Object> row = new HashMap<>();
						for(FieldVector fieldVector : fieldVectors) {
							String fieldName = fieldVector.getField().getName();
							Object fieldValue = this.getFieldValue(fieldVector,i);
							row.put(fieldName,fieldValue);
						}
						data.add(row);
					}
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

	Object getFieldValue(FieldVector fieldVector, int index) throws SQLException {
		Types.MinorType type = Types.getMinorTypeForArrowType(fieldVector.getField().getType());

		if(fieldVector.isNull(index)) {
			return null;
		}

		if (type == Types.MinorType.VARCHAR) {
			Text value = ((VarCharVector) fieldVector).getObject(index);
			if (value != null) return value.toString();
			return null;
		} else if (type == Types.MinorType.INT) {
			return ((IntVector) fieldVector).get(index);
		} else if (type == Types.MinorType.TINYINT) {
			return ((TinyIntVector) fieldVector).get(index);
		} else if (type == Types.MinorType.SMALLINT) {
			return ((SmallIntVector) fieldVector).get(index);
		} else if (type == Types.MinorType.DECIMAL) {
			return ((DecimalVector) fieldVector).getObject(index);
		} else if (type == Types.MinorType.FLOAT4) {
			return ((Float4Vector) fieldVector).getObject(index);
		} else if (type == Types.MinorType.FLOAT8) {
			return ((Float8Vector) fieldVector).getObject(index);
		} else if (type == Types.MinorType.BIGINT) {
			return ((BigIntVector) fieldVector).get(index);
		} else if (type == Types.MinorType.BIT) {
			return (int) ((BitVector) fieldVector).get(index) == 1;
		} else if (type == Types.MinorType.DATEDAY) {
			return ((DateDayVector)fieldVector).getObject(index);
		} else if (type == Types.MinorType.TIMENANO) {
			return ((TimeNanoVector) fieldVector).getObject(index);
		} else if (type == Types.MinorType.TIMESTAMPNANOTZ) {
			long epochNano = ((TimeStampNanoTZVector) fieldVector).getObject(index);
			String date = new java.text.SimpleDateFormat(Constants.DATE_ISO_STD)
					.format(new java.util.Date (epochNano/1000000));
			return date;
		} else if (type == Types.MinorType.TIMESTAMPNANO) {
			return ((TimeStampNanoVector) fieldVector).getObject(index);
		} else if (type == Types.MinorType.TIMESTAMPMILLITZ) {
			long epochMillis = ((TimeStampMilliTZVector) fieldVector).getObject(index);
			String date = new java.text.SimpleDateFormat(Constants.DATE_ISO_STD)
					.format(new java.util.Date (epochMillis));
			return date;
		} else if (type == Types.MinorType.TIMESTAMPMILLI) {
			return ((TimeStampMilliVector) fieldVector).getObject(index);
		}
		throw new SQLException(MessageFormat.format("Unknown arrow type {0}", type.name()));
	}
}
