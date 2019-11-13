# Copyright (C) 2019, International Business Machines Corporation. 
# All Rights Reserved.

.PHONY: build clean all

STREAMS_SPARKMLLIB_TOOLKIT ?=../../com.ibm.streamsx.sparkmllib:${STREAMS_INSTALL}/toolkits/com.ibm.streamsx.sparkmllib
ifeq ($(STREAMS_STUDIO_BUILDING), 1)
    $(info Building from Streams Studio, use env vars set by studio)
    SPLC = $(STREAMS_STUDIO_SC_PATH)
    DATA_DIR = $(STREAMS_STUDIO_DATA_DIRECTORY)
    OUTPUT_DIR = $(STREAMS_STUDIO_OUTPUT_DIRECTORY)
    TOOLKIT_PATH = $(STREAMS_STUDIO_SPL_PATH)
else
    $(info build use env settings)
    ifeq ($(STREAMS_INSTALL),)
        $(error error: environment variable STREAMS_INSTALL has to be set)
    endif
    SPLC = $(STREAMS_INSTALL)/bin/sc
    DATA_DIR = data
    OUTPUT_DIR = output
    TOOLKIT_PATH = $(STREAMS_SPARKMLLIB_TOOLKIT)
endif
SPLC_FLAGS ?= -a
SPL_CMD_ARGS ?= 
SPL_MAIN_COMPOSITE = sample::Main

build:
	$(SPLC) $(SPLC_FLAGS) -M $(SPL_MAIN_COMPOSITE) -t ${TOOLKIT_PATH} --data-directory $(DATA_DIR) --output-dir $(OUTPUT_DIR) $(SPL_CMD_ARGS)

clean:
	$(SPLC) $(SPLC_FLAGS) -C -M $(SPL_MAIN_COMPOSITE) -t ${TOOLKIT_PATH} --data-directory $(DATA_DIR) --output-dir $(OUTPUT_DIR) $(SPL_CMD_ARGS)

all: clean build