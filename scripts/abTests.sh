#!/usr/bin/env bash

set -e -x

ab -rl -c 100 -n 1000 -p account.json -v 0 -T 'application/json' "https://accountz.pcf1.fe.gopivotal.com/accounts"