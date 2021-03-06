package de.hybris.merchandise.loyaltysystem.core.services.impl;


import de.hybris.merchandise.loyaltysystem.core.daos.LoyaltyPointsConfigurationDAO;
import de.hybris.merchandise.loyaltysystem.core.services.CustomerLoyaltyPointsService;
import de.hybris.merchandise.loyaltysystem.model.LoyaltyPointsConfigurationModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wladek on 11/16/16.
 */
public class DefaultCustomerLoyaltyPointsService implements CustomerLoyaltyPointsService {

    private ModelService modelService;

    private LoyaltyPointsConfigurationDAO loyaltyPointsConfigurationDAO;

    @Override
    public void addLoyaltyPointsToCustomer(CustomerModel customerModel, OrderModel orderModel) {
        accureLoyaltyPoints(customerModel, orderModel, getLoyaltyPointsConfigurationModel(orderModel));
        modelService.save(customerModel);
    }

    private LoyaltyPointsConfigurationModel getLoyaltyPointsConfigurationModel(OrderModel orderModel){
        List<LoyaltyPointsConfigurationModel> configs = loyaltyPointsConfigurationDAO.findLoyaltyPointsConfigurationByCurrency(orderModel.getCurrency());
        if(configs == null || configs.isEmpty()){
            throw new IllegalArgumentException("there is no LoyaltyPointsConfiguration for this Currency");
        }
        return configs.get(0);
    }

    private void accureLoyaltyPoints(CustomerModel customerModel, OrderModel orderModel, LoyaltyPointsConfigurationModel configurationModel){
        Double customerExtraLoyaltyPoints = new Double(0.0);
        switch(configurationModel.getConfigurationType()){
            case ABSOLUTE:
                customerExtraLoyaltyPoints = configurationModel.getValue();
                break;
            case RELATIVE:
                customerExtraLoyaltyPoints = orderModel.getSubtotal() * ( configurationModel.getValue() / 100. );
                break;
            default:
                throw new IllegalArgumentException("incorrect value for LoyaltyPointsConfigurationType enumeration");
        }
        if(customerModel.getLoyaltyPoints() == null){
            customerModel.setLoyaltyPoints(Double.valueOf(0.0));
        }
        customerModel.setLoyaltyPoints(customerModel.getLoyaltyPoints() + customerExtraLoyaltyPoints);
    }

    public boolean checkLoyaltyPointsForOrder(CustomerModel customerModel, double amount){
        return customerModel.getLoyaltyPoints() >= amount;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setLoyaltyPointsConfigurationDAO(LoyaltyPointsConfigurationDAO loyaltyPointsConfigurationDAO) {
        this.loyaltyPointsConfigurationDAO = loyaltyPointsConfigurationDAO;
    }

}
