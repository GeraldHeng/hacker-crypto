package edu.singaporetech.btco

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.btco.databinding.ActivityLayoutBinding
import kotlinx.coroutines.*
import java.util.*

class BTCOActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityLayoutBinding

    private external fun chainMethod(difficulty: Int, message: String, blocks: Int): String
    private external fun genesisMethod(difficulty: Int): String

    /**
     * Init everything needed when created.
     * - set button listeners
     * @param savedInstanceState the usual bundle of joy
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.genesisButton.setOnClickListener {
            handleGenesisClicked()
        }

        binding.chainButton.setOnClickListener {
            handleChainClicked()
        }
    }

    /**
     * Handle genesis button logic
     *
     */
    private fun handleGenesisClicked() {
        if (validateMethodInputs(
                difficultyEmpty = true,
                difficultyOutOfRange = true
            )
        ) {
            var difficultyValue = binding.difficultyEditText.text.toString().toInt()

            launch {
                val start = System.currentTimeMillis()

                var hash: String
                withContext(Dispatchers.Default) {
                    hash = genesisMethod(difficultyValue)
                }

                val time = System.currentTimeMillis() - start
                binding.dataHashTextView.text = hash
                binding.logTextView.text = getString(R.string.time_taken_to_mine, time.toString())
                Log.e(TAG, "genesis ${time}ms $hash")
            }
        }
    }

    /**
     * Handle chain button logic
     *
     */
    private fun handleChainClicked() {
        if (validateMethodInputs(
                difficultyEmpty = true,
                difficultyOutOfRange = true,
                blockEmpty = true,
                blockOutOfRange = true,
                transactionEmpty = true
            )
        ) {
            var difficultyValue = binding.difficultyEditText.text.toString().toInt()
            var message = binding.msgEditText.text.toString()
            var blocks = binding.blocksEditText.text.toString().toInt()

            launch {
                val start = System.currentTimeMillis()

                var hash: String
                withContext(Dispatchers.Default) {
                    hash = chainMethod(difficultyValue, message, blocks)
                }

                val time = System.currentTimeMillis() - start
                binding.dataHashTextView.text = hash
                binding.logTextView.text = getString(R.string.time_taken_to_mine, time.toString())
                Log.e(TAG, "chain ${time}ms $hash")
            }
        }
    }

    /**
     * Validate buttons with the edittexts
     *
     * @param difficultyEmpty
     * @param difficultyOutOfRange
     * @param transactionEmpty
     * @param blockEmpty
     * @param blockOutOfRange
     * @return
     */
    private fun validateMethodInputs(
        difficultyEmpty: Boolean = false,
        difficultyOutOfRange: Boolean = false,
        transactionEmpty: Boolean = false,
        blockEmpty: Boolean = false,
        blockOutOfRange: Boolean = false
    ): Boolean {
        var errorMsg = ""
        var difficultyValue = binding.difficultyEditText.text.toString()
        var blockValue = binding.blocksEditText.text.toString()
        var transactionValue = binding.msgEditText.text.toString()

        // Difficulty validation.
        if (difficultyEmpty) {
            if (difficultyValue.isEmpty()) {
                errorMsg += DIFFICULTY_EMPTY + "\n"
            } else {
                if (difficultyOutOfRange) {
                    if (difficultyValue.toInt() < 1 ||
                        difficultyValue.toInt() > 10
                    ) {
                        errorMsg += DIFFICULTY_OUT_OF_RANGE + "\n"
                    }
                }
            }
        }

        // Block validation.
        if (blockEmpty) {
            if (blockValue.isEmpty()) {
                errorMsg += BLOCK_EMPTY + "\n"
            } else {
                if (blockOutOfRange) {
                    if (blockValue.toInt() < 2 ||
                        blockValue.toInt() > 888
                    ) {
                        errorMsg += BLOCK_OUT_OF_RANGE + "\n"
                    }
                }
            }
        }

        // Transaction validation.
        if (transactionEmpty) {
            if (transactionValue.isEmpty()) {
                errorMsg += TRANSACTION_EMPTY + "\n"
            }
        }

        if (errorMsg.isNotEmpty()) {
            binding.logTextView.text = errorMsg
        }

        return errorMsg.isEmpty()
    }


    companion object {
        private val TAG: String = BTCOActivity::class.simpleName.toString()
        private val DIFFICULTY_EMPTY: String = "difficulty cannot be empty..."
        private const val DIFFICULTY_OUT_OF_RANGE: String = "difficulty must be 1 to 10..."
        private const val TRANSACTION_EMPTY: String = "describe your transaction in words..."
        private const val BLOCK_EMPTY: String = "blocks cannot be empty..."
        private const val BLOCK_OUT_OF_RANGE: String = "blocks must be 2 to 888..."

        init {
            System.loadLibrary("btco")
        }
    }
}