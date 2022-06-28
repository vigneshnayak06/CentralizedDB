package analytics;

import java.io.IOException;
import java.util.regex.Pattern;

import Utility.Utility;

public class AnalyticsQueryParser {
	
	private String COUNT_QUERIES_KEYWORD = "count queries";
	private String COUNT_QUERIES_SYNTAX = "^(count\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_VALID_QUERIES_KEYWORD = "count valid queries";
	private String COUNT_VALID_QUERIES_SYNTAX = "^(count\\svalid\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_INVALID_QUERIES_KEYWORD = "count invalid queries";
	private String COUNT_INVALID_QUERIES_SYNTAX = "^(count\\sinvalid\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_UPDATE_QUERIES_KEYWORD = "count update queries";
	private String COUNT_UPDATE_QUERIES_SYNTAX = "^(count\\supdate\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_DELETE_QUERIES_KEYWORD = "count delete queries";
	private String COUNT_DELETE_QUERIES_SYNTAX = "^(count\\sdelete\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_SELECT_QUERIES_KEYWORD = "count select queries";
	private String COUNT_SELECT_QUERIES_SYNTAX = "^(count\\sselect\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_CREATE_QUERIES_KEYWORD = "count create queries";
	private String COUNT_CREATE_QUERIES_SYNTAX = "^(count\\screate\\squeries\\s[a-zA-Z\\d]+;)$";
	private String COUNT_INSERT_QUERIES_KEYWORD = "count insert queries";
	private String COUNT_INSERT_QUERIES_SYNTAX = "^(count\\sinsert\\squeries\\s[a-zA-Z\\d]+;)$";
	
	private AnalyticsQueryProcessor analyticsQueryProcessor= new AnalyticsQueryProcessor();
	
	public void parseAnalyticsQuery(String query) {
		
	    if (query == null || query.trim().isEmpty()) {
	      System.out.println("Invalid query syntax!");
	    }
	    
	    String queryLowerCase = query.trim().toLowerCase();
	    
	    if (queryLowerCase.contains(COUNT_QUERIES_KEYWORD)) {
	       boolean isCountQueriesSyntaxCorrect = Pattern.matches(COUNT_QUERIES_SYNTAX, queryLowerCase);
	      if (!isCountQueriesSyntaxCorrect) {
	    	  System.out.println("Invalid count queries syntax!");
	      }
	      else {
	    	  analyticsQueryProcessor.processCountQueries(queryLowerCase);
	      }
	    }
	    
	    else if (queryLowerCase.contains(COUNT_VALID_QUERIES_KEYWORD)) {
	       boolean isCountValidQueriesSyntaxCorrect = Pattern.matches(COUNT_VALID_QUERIES_SYNTAX, queryLowerCase);
	      if (!isCountValidQueriesSyntaxCorrect) {
	    	  System.out.println("Invalid count valid queries syntax!");
	      }
	      else {
	    	  analyticsQueryProcessor.processCountQueriesByStatus(queryLowerCase);
	      }
	    }
	    
	    else if (queryLowerCase.contains(COUNT_INVALID_QUERIES_KEYWORD)) {
	       boolean isCountInValidQueriesSyntaxCorrect = Pattern.matches(COUNT_INVALID_QUERIES_SYNTAX, queryLowerCase);
	      if (!isCountInValidQueriesSyntaxCorrect) {
	    	  System.out.println("Invalid count invalid queries syntax!");
	      }
	      else {
	    	  analyticsQueryProcessor.processCountQueriesByStatus(queryLowerCase);
	      }
	    }
	    
	    else if (queryLowerCase.contains(COUNT_UPDATE_QUERIES_KEYWORD)) {
	       boolean isCountUpdateQueriesSyntaxCorrect = Pattern.matches(COUNT_UPDATE_QUERIES_SYNTAX, queryLowerCase);
	      if (!isCountUpdateQueriesSyntaxCorrect) {
	    	  System.out.println("Invalid count update queries syntax!");
	      }
	      else {
	    	  analyticsQueryProcessor.processCountQueriesByOperation(queryLowerCase);
	      }
	    }
	    
	    else if (queryLowerCase.contains(COUNT_DELETE_QUERIES_KEYWORD)) {
	       boolean isCountDeleteQueriesSyntaxCorrect = Pattern.matches(COUNT_DELETE_QUERIES_SYNTAX, queryLowerCase);
	      if (!isCountDeleteQueriesSyntaxCorrect) {
	    	  System.out.println("Invalid count delete queries syntax!");
	      }
	      else {
	    	  analyticsQueryProcessor.processCountQueriesByOperation(queryLowerCase);
	      }
	    }
	    else if (queryLowerCase.contains(COUNT_SELECT_QUERIES_KEYWORD)) {
		       boolean isCountDeleteQueriesSyntaxCorrect = Pattern.matches(COUNT_SELECT_QUERIES_SYNTAX, queryLowerCase);
		      if (!isCountDeleteQueriesSyntaxCorrect) {
		    	  System.out.println("Invalid count select queries syntax!");
		      }
		      else {
		    	  analyticsQueryProcessor.processCountQueriesByOperation(queryLowerCase);
		      }
		    }
	    else if (queryLowerCase.contains(COUNT_CREATE_QUERIES_KEYWORD)) {
		       boolean isCountDeleteQueriesSyntaxCorrect = Pattern.matches(COUNT_CREATE_QUERIES_SYNTAX, queryLowerCase);
		      if (!isCountDeleteQueriesSyntaxCorrect) {
		    	  System.out.println("Invalid count create queries syntax!");
		      }
		      else {
		    	  analyticsQueryProcessor.processCountQueriesByOperation(queryLowerCase);
		      }
		    }
	    else if (queryLowerCase.contains(COUNT_INSERT_QUERIES_KEYWORD)) {
		       boolean isCountDeleteQueriesSyntaxCorrect = Pattern.matches(COUNT_INSERT_QUERIES_SYNTAX, queryLowerCase);
		      if (!isCountDeleteQueriesSyntaxCorrect) {
		    	  System.out.println("Invalid count insert queries syntax!");
		      }
		      else {
		    	  analyticsQueryProcessor.processCountQueriesByOperation(queryLowerCase);
		      }
		    }
	    else {
	    	System.out.println("Invalid query!");
	    }
	  }
	
	public void receiveQuery() throws IOException {
		
		String query;
		do {
			query = Utility.receiveInput();
        	if (!query.equals("exit;")) {
        		parseAnalyticsQuery(query);
        	}
    	} while(!query.equals("exit;"));

	}
	
//	public static void main(String[] args) {
//		AnalyticsQueryParser parser = new AnalyticsQueryParser();
//		parser.parseAnalyticsQuery("count update queries DB1;");
//	}
}
