package com.gogli.librarymanagementsystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportData {
    
    private List<Transactions> overdueTransactions;     
    private Map<Long, List<Transactions>> transactionsHistoryByPatronId;
}
