package com.authorizer.credit_card.adt

import com.authorizer.account.adt.Account
import com.authorizer.account.violation.Violation

case class TransactionResult(account: Account, violations: List[Violation])
