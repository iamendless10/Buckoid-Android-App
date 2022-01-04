package com.example.trackback.fragments

import android.annotation.SuppressLint
import android.app.TaskStackBuilder
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.trackback.BuildConfig
import com.example.trackback.MainActivity
import com.example.trackback.R
import com.example.trackback.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception

class Profile : Fragment() {

    lateinit var binding:FragmentProfileBinding
    lateinit var userDetails: SharedPreferences
    var isNight:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(inflater, container, false)
        setData()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        userDetails = requireActivity().getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
        nightMode()
        val name = userDetails.getString("Name", "")
        val monthlyBudget = userDetails.getString("MonthlyBudget","0")

        binding.name.text = name
        binding.monthlyBudget.text = "₹$monthlyBudget"

        binding.edit.setOnClickListener {
            openEditDialog(name,monthlyBudget)
        }
        binding.share.setOnClickListener{
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "@string/app_name")
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID
                )
                startActivity(Intent.createChooser(intent, "Share With"))
            } catch (e: Exception) {
                Toast.makeText(
                    requireActivity(),
                    "Unable to share at this moment.." + e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.rateUs.setOnClickListener{
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            }
        }

    }

    fun nightMode(){
        // Configure night-mode switch
        isNight = userDetails.getBoolean("nightMode",true)
       if (isNight) {
            binding.nightSwitchCompat.setChecked(true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            binding.nightSwitchCompat.setChecked(false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.nightSwitchCompat.setOnCheckedChangeListener { buttonView, isChecked -> applyNightMode(isChecked) }

    }

    private fun applyNightMode(checked: Boolean) {
        if (checked) {
            isNight = true
            saveSettingsBoolean("nightMode",true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            isNight = false
            saveSettingsBoolean("nightMode",false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    protected fun restartActivityInvalidateBackstack(activity: MainActivity) {
        val intent = Intent()
        intent.setClass(activity,MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(activity)
        stackBuilder.addNextIntentWithParentStack(intent)
        stackBuilder.startActivities(Bundle())
    }
    private fun saveSettingsBoolean(mode: String, isNight: Boolean) {
        val editor: SharedPreferences.Editor = userDetails.edit()
        editor.putBoolean(mode, isNight)
        editor.apply()
        restartActivityInvalidateBackstack(requireActivity() as MainActivity)
    }

    private fun openEditDialog(name: String?, monthlyBudget: String?) {
        val bottomDialog: BottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.bottom_dialog)
        bottomDialog.setContentView(R.layout.update_user_details_dialog)

        val update = bottomDialog.findViewById<Button>(R.id.update)
        val cancel = bottomDialog.findViewById<Button>(R.id.cancel)
        val nameEditor = bottomDialog.findViewById<TextInputEditText>(R.id.edit_name)
        val moneyEditor = bottomDialog.findViewById<TextInputEditText>(R.id.edit_money)

        nameEditor?.setText(name)
        moneyEditor?.setText(monthlyBudget)

        update?.setOnClickListener {
            val name = nameEditor?.text.toString()
            val monthly_budget = moneyEditor?.text.toString()
            if(name.equals("") || monthly_budget.equals("")) {
                Toast.makeText(requireActivity(), "Name and Budget Cant be empty...", Toast.LENGTH_SHORT).show()
            }else{
                val editor: SharedPreferences.Editor = userDetails.edit()
                editor.putString("Name", name)
                editor.putString("MonthlyBudget", monthly_budget)
                editor.apply()
                setData()
                bottomDialog.dismiss()
            }

        }
        cancel?.setOnClickListener {
            bottomDialog.dismiss()
        }

        bottomDialog.show()

    }



}