FROM centos:centos7
ENV VERSION=1.2.0
RUN yum install -y zip && yum install -y unzip
RUN yum install -y python3
COPY zip_job.py /tmp
CMD echo OS Type: "$(cat /etc/redhat-release)" ; test -f /tmp/zip_job.py && echo "zip_job.py exists on /tmp dir."  \
    || echo "zip_job.py does not exist on /tmp dir." ; cat