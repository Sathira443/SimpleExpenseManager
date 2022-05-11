/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest{
    private static ExpenseManager expenseManager;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        expenseManager = new PersistentExpenseManager(context);
    }

    @Test
    public void testAddAccount() {
        expenseManager.addAccount("Sasara123", "Yoda Bank", "Anakin Skywalker", 10000.0);
        List<String> Accounts = expenseManager.getAccountNumbersList();
        assertTrue(Accounts.contains("Sasara123"));
    }

    @Test
    public void testExpenseTransactions() throws InvalidAccountException {
        String accountNo = "190059PTesting5";
        String expense = "55.00";

        expenseManager.addAccount(accountNo, "People's", "Sonam", 7500);

        double previousBalance = expenseManager.getAccountsDAO().getAccount(accountNo).getBalance();
        expenseManager.updateAccountBalance(accountNo, 11, 5, 2022, ExpenseType.EXPENSE, expense);

        double newAmount = expenseManager.getAccountsDAO().getAccount(accountNo).getBalance();
//        Log.d("new", String.valueOf(newAmount));
//        Log.d("previous", String.valueOf(previousBalance));
        assertTrue(newAmount == previousBalance - (Double.parseDouble(expense)));

    }

    @Test
    public void testIncomeTransactions() throws InvalidAccountException {
        String accountNo="190359PTestingIncome";
        String income="180.00";

        expenseManager.addAccount(accountNo,"Commercial","Sonam",5000);

        double previousBalance = expenseManager.getAccountsDAO().getAccount(accountNo).getBalance();
        expenseManager.updateAccountBalance(accountNo,24, 5, 2022, ExpenseType.INCOME, income) ;

        double currentBalance=expenseManager.getAccountsDAO().getAccount(accountNo).getBalance();

        assertTrue(currentBalance== Double.parseDouble(income) + previousBalance);
    }
}