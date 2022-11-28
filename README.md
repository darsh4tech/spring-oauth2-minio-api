# spring-oauth2-minio-api
## Teck Stack:
Spring boot , oauth2 , liquibase , Minio-api for file management , Postgres DB and Pgadmin using docker compose file

1) The flow of the project is the normal oauth2 flow where i am using Password grant_type
2) I am using refreshing mechanism and as we all know the refreshing mechanism is responsibilty of client so my client in my case was Postman
so, within the collection in the repo you will find [ pre-request ] script to check whether the access_token expired or not
i mean you didn't need to call the request for token api just call the resource server's endpoint directly and it will work as inside the authorization header I passed the collection variable that i set the token insdie when the token expired

3) i defined liquibase script to define 2-admin users [Rohan , Bushra] so i can use them to request token from the server

4) the resource server deal with file management so, i used Minio's Api where Minio allows the upload and download of files for containerized applications, respecting the interfaces of Amazon S3 solution using the terms of amazon S3 like bucket but upload your files to your own server not on the cloud 
but in my case instead of put extra configuration as it is just task i used the public server of minio company which is https://play.min.io/minio/
where: 
   1) Access Key : Q3AM3UQ867SPQQA43P2F
   2) Secret Key : zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG

5) I added Unit Test and Integration Test to my code using Junit 5 (jupiter) and mockito

6) I added intergration test and mocked the the security layer above my resource server by creating dummy authentication server using the code 

7) As i was using docker compose so: 
   1) run my containers using [ docker compose up ]
   2) run [ docker ps ] to fetch the container_id value s to be used later
   3) run this command [ docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ??_my_container_id_?? ]
   4) replace the container id within the command to get the IP of running container so i can put the postgres IP in my application.properties
   5) use the IP of Pgadmin with port 80 to view my DB on the browser 
