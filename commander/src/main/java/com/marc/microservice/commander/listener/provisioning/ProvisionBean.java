package com.marc.microservice.commander.listener.provisioning;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisionBean {
	private String cartUuid;
}
