@Library('sfci-pipeline-sharedlib@master') _

import net.sfdc.dci.BuildUtils
import net.sfdc.dci.MavenUtils
import net.sfdc.dci.LogUtil
import net.sfdc.dci.CodeCoverageUtils

env.GUS_TEAM_NAME = "CDP Query Service"
env.GUS_TEAM_EMAIL = "cdpqueryservice@salesforce.com"

env.RELEASE_BRANCHES = ['master']

// branches that will publish SNAPSHOT releases from the a360 instance
def SNAPSHOT_BRANCHES = []

// Careful of changing the default, or relying on dynamically resolving them
// https://issues.jenkins-ci.org/browse/JENKINS-41929
def buildParameters = {
  parameters([
    booleanParam(
      name: 'SKIP_TESTS',
      description: 'Do you want to skip the tests?',
      defaultValue: false
    ),
    booleanParam(
      name: 'PUSH_TO_NEXUS',
      description: 'Do we want to put artifact in nexus',
      defaultValue: false
    )
  ])
}

def releaseParameters = {
  parameters([
    booleanParam(
      name: 'SKIP_TESTS',
      description: 'Do you want to skip the tests?',
      defaultValue: false
    ),
    booleanParam(
      name: 'PUSH_TO_NEXUS',
      description: 'Do we want to put artifact in nexus',
      defaultValue: false //Should this be true for release branch?
    ),
    booleanParam(
      name: 'IS_RELEASE',
      description: 'Do you want to release?',
      // Check it on the UI to release with automatic versioning.
      defaultValue: false 
    ),
    string( defaultValue: MavenUtils.getDefaultReleaseVersion(this),
      description: 'Enter the release version',
      name: 'RELEASE_VERSION'
    ),
    string( defaultValue: "",
      description: 'Next Release Version, e.g. RELEASE_VERSION=cdp_2020.4a -> NEXT_RELEASE_VERSION=cdp_2020.4b-SNAPSHOT',
      name: 'NEXT_RELEASE_VERSION'
    )
  ])
}

/*** TESTING CONFIG ***/
env.CODE_COVERAGE_THRESHOLD = 0
// These patterns must mirror those found in the pom.xml under the jacoco config

def inclusionPatterns = [
]

def exclusionPatterns = [
]

// Reference: https://git.soma.salesforce.com/dci/sfci-pipeline-sharedlib/blob/master/src/net/sfdc/dci/v1/ExecutePipeline.groovy
def envDef = [
  releaseParameters: releaseParameters,
  buildParameters: buildParameters,
  buildImage: 'ops0-artifactrepo1-0-prd.data.sfdc.net/dci/centos7-sfci-jdk11-maven:80795e9',
  useOpenJDK: true,
  emailTo: env.GUS_TEAM_EMAIL,
  stopSuccessEmail: true
  // pipelineTriggerCron: { cron('@daily') }
]

def getMavenVersion(isSnapshotBuild) {
  // Always use the version from pom.xml
  def version = MavenUtils.getDefaultVersion(this)

  if (version == null) {
    failAndThrow(new Exception('Version resolved as null!'))
  }

  if (isSnapshotBuild && !version.endsWith('-SNAPSHOT')) {
    version += '-SNAPSHOT'
  }

  // Overwrite the RELEASE_VERSION param
  env.RELEASE_VERSION = version
  return version
}

executePipeline(envDef) {
  def isRelease = env.IS_RELEASE == 'true'
  def toNexus = env.PUSH_TO_NEXUS == 'true'
  def runTests = env.SKIP_TESTS != 'true'
  def isReleaseBuild = BuildUtils.isReleaseBuild(env)
  def isPullRequestBuild = BuildUtils.isPullRequestBuild(env)
  def isSnapshotBuild = SNAPSHOT_BRANCHES.contains(env.BRANCH_NAME)
  def version = getMavenVersion(isSnapshotBuild)

  LogUtil.info(this, "Config for build: isRelease: ${isRelease}, isReleaseBuild: ${isReleaseBuild}, BRANCH_NAME: ${env.BRANCH_NAME}, isPullRequestBuild: ${isPullRequestBuild}, isSnapshotBuild: ${isSnapshotBuild}, version: ${version}")

  stage('Init') {
    checkout scm
    
    //Magic value, hidden logic around this parameter.
    env.RELEASE = 'true'

    currentBuild.displayName = "#${BUILD_NUMBER}: ${version}"
    mavenInit()
  }

  service: {
    stage('Compile') {
      mavenBuild([
        maven_goals: 'clean compile'
      ])
    }

    LogUtil.info(this, "Compile stage completed")

    stage('Verify') {
      mavenBuild([
        maven_goals: 'deploy', 
        maven_args: '-Prelease ' + (runTests ? '-Pcode-coverage-jacoco' : '-DskipTests')
      ])

      if(runTests) {
         def coverage_config = [
          tool_name: 'jacoco',
          gus_team_name: env.GUS_TEAM_NAME,
          failing_threshold: env.CODE_COVERAGE_THRESHOLD,
          inclusion_patterns: inclusionPatterns.join(','),
          exclusion_patterns: exclusionPatterns.join(',')
        ]

        LogUtil.info(this, "Validating code coverage")
        CodeCoverageUtils.publishAndValidateCoverageReport(this, coverage_config)
        if(isRelease) {
          LogUtil.info(this, "Uploading code coverage report to GUS")
          CodeCoverageUtils.uploadReportForGusDashboard(this, coverage_config)
        }
      } else {
         mavenBuild([
           maven_goals: 'deploy',
           maven_args: '-Prelease -DskipTests'
         ])
       }
    }

    LogUtil.info(this, "Verify stage completed")

    if(BuildUtils.isReleaseBuild(env)) {
    stage('Build') {
        def maven_config = [
          maven_goals: 'deploy',
          maven_args: '-Prelease -DskipTests'
        ]

        if(isRelease) {
          mavenReleasePrepare(maven_config)

          if(toNexus) {
            mavenReleasePerform(maven_config)
          }
        } else if (toNexus) {
          mavenDeploySnapshots(maven_config)
        } else {
          mavenBuild(maven_config)
        }
      }
    }
  }

  if(isRelease) {
    stage('GUS compliance') {
      git2gus()
    }
  }
}
