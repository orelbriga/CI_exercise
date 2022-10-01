#!/bin/sh

echo OS Type: "$(cat /etc/redhat-release)"
test -f /tmp/zip_job.py && echo "zip_job.py exists on /tmp dir." || echo "zip_job.py does not exist on /tmp dir."
cat
