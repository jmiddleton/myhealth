To build the project execute the following steps:

1.- open Boot2Docker
2.- cd /[PATH]/myhealth-api
3.- check that ./src/main/resources/docker/Dockerfile is correct:
	.- check memcached url
	.- check jar version
4.- mvn clean package docker:build -Dmaven.test.skip=true
	
5.- touch target/docker/Dockerfile target/docker/myhealth-api-[VERSION].jar
(this is because the maven plugin change the timestamp to 01/01/1970)

6.- Create a ZIP file with a compatible structure for AWS Beanstalk
	cd ./target/docker
	zip myhealth-api_v[VERSION].zip *
	
7.- deploy the zip file on AWS Beanstalk