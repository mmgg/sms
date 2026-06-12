package com.clwu.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellingPricePageController {
    @GetMapping("/selling-price")
    public String sellingPrice() {
        return "selling_price/list";
    }
}
