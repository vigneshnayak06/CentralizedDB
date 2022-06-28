package analytics;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnalyticsQueryPredicates {
	
	public static Predicate<QueryLogResponse> countQueries(String database) {
        return p -> p.getDatabase().equalsIgnoreCase(database);
    }
     
	public static Predicate<QueryLogResponse> countQueriesByStatus(String database, String status) {
        return p -> p.getDatabase().equalsIgnoreCase(database) && p.getStatus().equalsIgnoreCase(status);
    }
	
	public static Predicate<QueryLogResponse> countQueriesByOperation(String database, String operation, String table) {
        return p -> p.getDatabase().equalsIgnoreCase(database) && p.getOperation().equalsIgnoreCase(operation) && p.getTable().equalsIgnoreCase(table); 
    }
	
    public static List<QueryLogResponse> filterQueries (List<QueryLogResponse> queries, 
                                                Predicate<QueryLogResponse> predicate) 
    {
        return queries.stream()
                    .filter( predicate )
                    .collect(Collectors.<QueryLogResponse>toList());
    }
}
