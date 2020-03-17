#!/usr/bin/env bash

set -e -x

ab -r -l -c 250 -n 90000  -v 0 -T 'application/json' "https://account-homochrome-melammed.pcf1.fe.gopivotal.com/foo" &
