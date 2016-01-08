package in.com.v2kart.facades.wallet.populators;

import java.text.SimpleDateFormat;

import org.springframework.util.Assert;

import in.com.v2kart.core.model.V2CustomerWalletTransactionModel;
import in.com.v2kart.facades.wallet.data.V2WalletTransactionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class V2CustomerWalletTransactionPopulator implements Populator<V2CustomerWalletTransactionModel,V2WalletTransactionData>{
	
	private static String DATE_FORMAT = "dd/MM/yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static Integer MULTIPLIER=-1;

	@Override
	public void populate(V2CustomerWalletTransactionModel source,
			V2WalletTransactionData target) throws ConversionException {
		
		
		Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        
        target.setTransactionDate(simpleDateFormat.format(source.getPostedDate()));
        target.setDescription(source.getDescription());
        if(source.getValue()<0){
        	target.setDebit(source.getValue()*MULTIPLIER);
        	target.setCredit(null);
        }else{
        	target.setDebit(null);
        	target.setCredit(source.getValue());
        }
		
	}

}
