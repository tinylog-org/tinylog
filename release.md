Releasing a New Version
=======================

Preconditions
-------------

 * Repository https://github.com/pmwmedia/tinylog.git cloned and actual
 * JDK 8 and JDK 9 installed
 * Maven 3 installed
 * GPG installed
 * Certificate for oss.sonatype.org imported
 * Passphrase and path to executable for GPG set in settings.xml
 * Username and password for oss.sonatype.org and tinylog's FTP server set in settings.xml

Steps for Each Release
----------------------

 1. Set new version: mvn versions:set -DnewVersion=VERSION -DgenerateBackupPoms=false
 2. Create a tag with new version number as name
 3. Deploy with JDK 9: mvn clean install deploy -P release
 4. Login to https://oss.sonatype.org/ to close and release the created repository under Staging Repositories
 5. Upload P2 repository: mvn p2:site wagon:upload -P release --non-recursive
 6. Upload ZIP archives from target to website
 7. Generate Javadoc for tinylog API with JDK 8: mvn javadoc:javadoc
 8. Upload generated Javadoc for tinylog API to website
 9. Restore snapshot version: mvn versions:set -DnewVersion=2.MINOR-SNAPSHOT -DgenerateBackupPoms=false
10. Push all commits and tags
11. Create release on GitHub with ZIP archives from target
12. Update tinylog version on website
13. Release a new post on website
