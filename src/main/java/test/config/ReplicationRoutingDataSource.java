//package test.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//@Slf4j
//public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
//    public static final String DATASOURCE_KEY_MASTER = "master";
//    public static final String DATASOURCE_KEY_SLAVE = "slave";
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
//        String dataSourceKey = (isReadOnly) ? DATASOURCE_KEY_SLAVE : DATASOURCE_KEY_MASTER;
//        log.info("Selected DataSource: {}", dataSourceKey);
//        return dataSourceKey;
//    }
//}