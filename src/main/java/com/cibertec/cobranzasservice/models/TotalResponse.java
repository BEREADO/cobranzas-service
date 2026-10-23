package com.cibertec.cobranzasservice.models;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalResponse {

    private BigDecimal total;
}
