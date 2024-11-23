package me.safex

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.safex.ui.theme.SafexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 你自定义的函数，这里保留不变
        setContent {
            SafexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // 调用添加账户的逻辑
        addDummyAccount()
    }

    private fun addDummyAccount() {
        val accountManager = AccountManager.get(this)
        val accountType = "com.example.account" // 替换为你的账户类型
        val accountName = "dummy@example.com"

        val account = Account(accountName, accountType)
        val password = "password" // 用于添加账户的密码

        // 添加账户
        val accountAdded = accountManager.addAccountExplicitly(account, password, null)

        if (accountAdded) {
            println("Account added successfully!")
        } else {
            println("Failed to add account.")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SafexTheme {
        Greeting("Android")
    }
}