package com.training.ms.cloud.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping("currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurency(@PathVariable String from ,
			@PathVariable String to , @PathVariable BigDecimal quantity) {
		
		Map<String,String> urivariables = new HashMap<>();
		urivariables.put("from", from);
		urivariables.put("to", to);
		ResponseEntity<CurrencyConversionBean> response = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}"
				, CurrencyConversionBean.class,urivariables );
		
		CurrencyConversionBean res = response.getBody();
		
		logger.info(" {} -> " , res);
		
		return new CurrencyConversionBean(1L,from,to,res.getConversionMultiple(),
				quantity,quantity.multiply(res.getConversionMultiple()),res.getPort());
	}
	
	
	// Above problem with Feign instead of RestTemplate
	@GetMapping("currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurencyFeign(@PathVariable String from ,
			@PathVariable String to , @PathVariable BigDecimal quantity) {
		
		CurrencyConversionBean res = proxy.retrieveExchangeValue(from, to);
		
		logger.info(" {} -> " , res);
		
		return new CurrencyConversionBean(1L,from,to,res.getConversionMultiple(),
				quantity,quantity.multiply(res.getConversionMultiple()),res.getPort());
	}
}
