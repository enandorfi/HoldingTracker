package com.transactions;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HoldingCalculator {
    Map<String,List<Holding>> calculateHoldings(File transactionFile, LocalDate date);
}

