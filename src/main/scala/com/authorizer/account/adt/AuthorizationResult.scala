package com.authorizer.account.adt

import com.authorizer.account.violation.Violation

case class AuthorizationResult(account: Option[Account], violations: Set[Violation])
