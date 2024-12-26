package com.example.statement.feignclient;

import com.example.statement.dto.LoanOfferDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "offer-service", url = "${deal.service.offer.base-url}")
public interface OfferClient {

    @PostMapping
    void selectOffer(@RequestBody LoanOfferDto offerDto);
}

