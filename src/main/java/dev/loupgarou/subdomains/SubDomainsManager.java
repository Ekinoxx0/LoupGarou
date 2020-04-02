package dev.loupgarou.subdomains;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import dev.loupgarou.MainLg;
import dev.loupgarou.subdomains.OVHApi.ZoneRecord;
import lombok.NonNull;

public class SubDomainsManager {
	
	private static final String selectedDomain = "wondalia.com";
	private static final String baseDomain = ".lg";
	private static final String ip = "51.178.86.153";
	private final OVHApi ovh;

	public SubDomainsManager() throws Exception {
		this.ovh = new OVHApi();
		
		//Test
		JsonElement domains = this.ovh.domainZone();
		if(!domains.isJsonArray()) throw new IllegalStateException("Response from OVH is invalid.");
		JsonArray domainArray = domains.getAsJsonArray();
		
		boolean findedSelectedDomain = false;
		for(JsonElement domainJson : domainArray) {
			String domain = domainJson.getAsString();
			if(domain.equals(selectedDomain)) findedSelectedDomain = true;
		}
		
		if(!findedSelectedDomain) throw new IllegalStateException("No selected domain in ovh response.");
		this.clearSubdomains();
		this.createSubdomain("test");
	}
	
	public void clearSubdomains() throws Exception {
		JsonElement recordsJson = ovh.domainZoneRecord(selectedDomain);
		if(!recordsJson.isJsonArray()) throw new Exception("Not an array : " + recordsJson);
		
		JsonArray arrayRecords = recordsJson.getAsJsonArray();
		for(JsonElement el : arrayRecords) {
			if(el.isJsonPrimitive()) {
				JsonPrimitive primi = (JsonPrimitive) el;
				if (primi.isNumber()) {
					JsonElement recordDetailsElement = ovh.domainZoneRecord("wondalia.com", primi.getAsLong());
					if(!recordDetailsElement.isJsonObject())
						throw new Exception("Not an object : " + recordDetailsElement);
					
					JsonObject recordDetails = recordDetailsElement.getAsJsonObject();
					if(!recordDetails.has("subDomain"))
						throw new Exception("clearSubdomains() : Doesn't contains subDomain");
					
					String subDomain = recordDetails.get("subDomain").getAsString();
					if(!subDomain.contains(baseDomain)) continue;
					
					long id = recordDetails.get("id").getAsLong();
					ovh.domainZoneRecordDelete(selectedDomain, id);
					MainLg.debug("Deleting subdomain : " + subDomain + "." + selectedDomain);
				}
			}
		}
		
		this.ovh.domainZoneRefresh(selectedDomain);
	}
	
	public boolean createSubdomain(@NonNull String domain) {
		JsonElement createResponseElement = this.ovh.domainZoneRecordPut(selectedDomain, new ZoneRecord("A", domain + baseDomain, ip));
		if(!createResponseElement.isJsonObject()) {
			MainLg.debug("createSubdomain() : Not a JsonObject -> " + createResponseElement);
			return false;
		}
		
		JsonObject createResponse = createResponseElement.getAsJsonObject();
		if(!createResponse.has("subDomain")) {
			MainLg.debug("createSubdomain() : Doesn't contains subDomain -> " + createResponseElement);
			return false;
		}
		
		this.ovh.domainZoneRefresh(selectedDomain);
		MainLg.debug("Created subdomain : " + domain + baseDomain + "." + selectedDomain);
		return createResponse.get("subDomain").getAsString().equals(domain + baseDomain);
	}
	
	public void deleteSubdomain(@NonNull String domain) {
		try {
			JsonElement recordsJson = ovh.domainZoneRecord(selectedDomain);
			if(!recordsJson.isJsonArray()) throw new Exception("Not an array : " + recordsJson);
			
			JsonArray arrayRecords = recordsJson.getAsJsonArray();
			for(JsonElement el : arrayRecords) {
				if(el.isJsonPrimitive()) {
					JsonPrimitive primi = (JsonPrimitive) el;
					if (primi.isNumber()) {
						JsonElement recordDetailsElement = ovh.domainZoneRecord("wondalia.com", primi.getAsLong());
						if(!recordDetailsElement.isJsonObject())
							throw new Exception("Not an object : " + recordDetailsElement);
						
						JsonObject recordDetails = recordDetailsElement.getAsJsonObject();
						if(!recordDetails.has("subDomain"))
							throw new Exception("clearSubdomains() : Doesn't contains subDomain");
						
						String subDomain = recordDetails.get("subDomain").getAsString();
						if(!subDomain.equals(domain + baseDomain)) continue;
						
						long id = recordDetails.get("id").getAsLong();
						ovh.domainZoneRecordDelete(selectedDomain, id);
						MainLg.debug("Deleting subdomain : " + subDomain + "." + selectedDomain);
					}
				}
			}
			
			this.ovh.domainZoneRefresh(selectedDomain);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
