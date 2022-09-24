FROM centos:centos7
ENV VERSION=1.2.0
RUN yum install -y zip && yum install -y unzip
RUN yum install -y python3
COPY zip_job.py /tmp
COPY onStartUp.sh .
ENTRYPOINT ["./onStartUp.sh"]