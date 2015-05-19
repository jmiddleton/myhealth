#!/bin/sh

touch app/index.html

grunt build

aws s3 sync ./dist s3://myhealth.puntanegra.info --delete --acl public-read --storage-class REDUCED_REDUNDANCY

aws s3api put-object --bucket myhealth.puntanegra.info --acl public-read --key scripts/vendor.gz.js --body ./dist/scripts/vendor.gz.js --content-encoding gzip --content-type application/javascript
aws s3api put-object --bucket myhealth.puntanegra.info --acl public-read --key scripts/scripts.gz.js --body ./dist/scripts/scripts.gz.js --content-encoding gzip --content-type application/javascript
aws s3api put-object --bucket myhealth.puntanegra.info --acl public-read --key styles/vendor.gz.css --body ./dist/styles/vendor.gz.css --content-encoding gzip --content-type text/css
aws s3api put-object --bucket myhealth.puntanegra.info --acl public-read --key styles/main.gz.css --body ./dist/styles/main.gz.css --content-encoding gzip --content-type text/css

