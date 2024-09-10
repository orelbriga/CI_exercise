Hi, 

Below are the instructions, please go over the task and see if you understand it all, contact me for any question.
1.	Create Dockerfile as follows: 
  a.	Based on latest centos7

  b.	Define env variable VERSION=1.2.0 

  c.	Install python 

  d.	Install zip 

  e.	Install unzip 

  f.	Copy zip_job.py into the image’s /tmp folder 

  g.	Once docker container is up run a command which will print OS type + verify the zip_job.py exists 

3.	Create zip_job.py python script as follows: 
    a.	Create an array a,b,c,d
  	
    b.	Based on this array create txt files (a.txt, b.txt...)
  	
    c.	Make sure all txt files created, if not fail the script
  	
    d.	Create zip files with names based on array + VERSION env variable, so each zip file will include one txt file inside:
          a_1.2.0.zip should include a.txt,
          b_1.2.0.zip should include b.txt
  	
    e.	Make sure all zip files are created, if not fail the script 
5.	Create java or kotlin project (gradle project) that compiling simple hello-world app (can be pulled from github.com)
6.	Install minikube (k8s cluster running on your station) or use any other k8s cluster. 
7.	Deploy jenkins as a service into it 
8.	Create a scripted pipeline running on agent described in #1 
9.	Build step should execute #2 
10.	Create a scripted pipeline running on agent based on gradle (take it from docker hub) 
11.	Build step should compile #3, build docker image from your app and push it to registry (local registry or any other registry) 
12.	Prepare some yaml file/files for deploying the app to k8s 
13.	Any additional build stage is welcomed 😊 
What I want to see: 
•	Jenkins pod running 
•	Once we trigger the job, new pod is starting and running whatever is written in the pipeline 
•	Clear and informative log in the console output of the jenkins job 
•	You will deploy your app into minikube (the app should run in tomcat or as a springboot app) 
*** the zip_job.py script can be written in any language (dependencies should be installed accordingly) the python is just for the example. 


Nikita

