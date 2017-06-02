package edu.wlu.graffiti.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;

@RestController
public class JsonController {
	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;

	@RequestMapping("/graffito/AGP-{edrId}/json")
	public Inscription getInscription(@PathVariable String edrId) {
		return graffitiDao.getInscriptionByEDR(edrId);
	}

	@RequestMapping("/all/json")
	public List<Inscription> getInscriptions() {
		return graffitiDao.getAllInscriptions();
	}

	@RequestMapping("/property/{city}/{insula}/{property}/json")
	public Property getProperty(@PathVariable String city, @PathVariable String insula, @PathVariable String property) {
		return findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
	}

}
