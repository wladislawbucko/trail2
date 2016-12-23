package de.hybris.merchandise.loyaltysystem.commerceservices.payment.impl;

import de.hybris.merchandise.loyaltysystem.commerceservices.payment.LoyaltyPointsPaymentService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * Created by wladek on 12/22/16.
 */
public class LoyaltyPointsPaymentServiceImpl implements LoyaltyPointsPaymentService{

    @Resource
    private ModelService modelService;

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public void payForOrderByLoyaltyPoints(CustomerModel customerModel, BigDecimal amount) {
        if(checkLoyaltyPointsForOrder(customerModel, amount)){
                if(payByLoyaltyPoints(customerModel, amount)) {
                modelService.save(customerModel);
            }
        }else{
            throw new AdapterException("Don't have enough Loyalty Points to pay for Order");
        }
    }

    private boolean checkLoyaltyPointsForOrder(CustomerModel customerModel, BigDecimal amount){
        return customerModel.getLoyaltyPoints() >= amount.doubleValue();
    }

    private boolean payByLoyaltyPoints(CustomerModel customerModel, BigDecimal amount){
        customerModel.setLoyaltyPoints(customerModel.getLoyaltyPoints() - amount.doubleValue());
        if(customerModel.getLoyaltyPoints() < 0.){
            throw new AdapterException("Don't have enough Loyalty Points to pay for Order after trying pay");
        }else{
            return true;
        }
    }

}
