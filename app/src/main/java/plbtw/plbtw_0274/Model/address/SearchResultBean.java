package plbtw.plbtw_0274.Model.address;

import java.util.List;

public class SearchResultBean {
	List<AddressComponentBean> address_components;
	String formatted_address;
	GeometryBean geometry;
	
	public List<AddressComponentBean> getAddress_components() {
		return address_components;
	}
	public void setAddress_components(List<AddressComponentBean> address_components) {
		this.address_components = address_components;
	}
	public String getFormatted_address() {
		return formatted_address;
	}
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	public GeometryBean getGeometry() {
		return geometry;
	}
	public void setGeometry(GeometryBean geometry) {
		this.geometry = geometry;
	}
	
	
}
