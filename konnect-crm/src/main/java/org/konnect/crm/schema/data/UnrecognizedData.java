package org.konnect.crm.schema.data;


import org.konnect.crm.schema.CrmBaseData;
import org.konnect.crm.schema.CrmDataType;

public class UnrecognizedData extends CrmBaseData {

    @Override
    public CrmDataType getDataType() {
        return null;
    }
}
