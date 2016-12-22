# Copyright (C) 2016, International Business Machines Corporation. 
# All Rights Reserved.

.PHONY: all distributed standalone clean

ifeq ($(STREAMS_INSTALL),)
    $(error error: environment variable STREAMS_INSTALL has to be set)
endif

STREAMS_SPARKMLLIB_TOOLKIT ?=../../com.ibm.streamsx.sparkmllib:${STREAMS_INSTALL}/toolkits/com.ibm.streamsx.sparkMLLib
SPLC_FLAGS ?= -a -t $(STREAMS_SPARKMLLIB_TOOLKIT)
SPLC = $(STREAMS_INSTALL)/bin/sc
SPL_CMD_ARGS ?= 
SPL_MAIN_COMPOSITE = sample::Main
#DATA_DIR=$(abspath ./data)
DATA_DIR=./data

all: clean distributed

distributed:
	$(SPLC) $(SPLC_FLAGS) -M $(SPL_MAIN_COMPOSITE) --data-directory $(DATA_DIR) $(SPL_CMD_ARGS)

standalone:
	$(SPLC) $(SPLC_FLAGS) -T -M $(SPL_MAIN_COMPOSITE) --data-directory $(DATA_DIR) $(SPL_CMD_ARGS)

clean:
	$(SPLC) $(SPLC_FLAGS) -C -M $(SPL_MAIN_COMPOSITE) --data-directory $(DATA_DIR)
