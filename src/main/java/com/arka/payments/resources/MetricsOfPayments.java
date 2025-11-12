package com.arka.payments.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsOfPayments {

    private Integer total;
    private Integer totalAccepted;
    private Integer totalCanceled;
    private Integer totalDeclined;
    private Integer totalPaymentByCreditCard;
    private Integer totalPaymentByPaypal;
    private Integer totalByPse;
    private Double max;
    private Double min;
    private Double sum;
    private Double avg;
}

