package com.marc.microservice.provisioning.ws;

import lombok.Getter;
import lombok.Setter;

import com.marc.microservice.provisioning.entity.Provision;

@Getter
@Setter
public class ProvisioningWS {

	private String uuid;
	private String cartUuid;

	public static ProvisioningWS fromEntity(Provision provision) {
		ProvisioningWS bean = new ProvisioningWS();
		bean.setUuid(provision.getUuid());
		bean.setCartUuid(provision.getCartUuid());
		return bean;
	}
}
