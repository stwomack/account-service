#!/usr/bin/env bash

set -e -x

curl -i -k -X POST -H "Content-Type:application/json" -d '{  "accountName" : "Snowden",  "accountType": "Savings"  }' https://accountz.pcf1.fe.gopivotal.com/accounts