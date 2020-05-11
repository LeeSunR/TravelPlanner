package com.leesunr.travelplanner.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.activity.LoginActivity
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private var mContext: Context? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)

        getLocation()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        exchange_button.setOnClickListener {
            val builder = AlertDialog.Builder(ContextThemeWrapper(mContext,
                R.style.Theme_AppCompat_Light_Dialog
            ))
            builder.setItems(R.array.country, object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, pos: Int) {
                    val countryWord = resources.getStringArray(R.array.country_word)[pos]
                    val countryName = resources.getStringArray(R.array.country)[pos]
                    val client = OkHttpClient()
                    val queryPair:String = "KRW"+countryWord.toUpperCase()
                    val apiUrl:String = "https://earthquake.kr:23490/query/$queryPair"
                    val request = Request.Builder().url(apiUrl).build()
                    client.newCall(request).enqueue(object :Callback{
                        override fun onResponse(call: Call?, response: Response?) {
                            val jsonObject = JSONObject(response?.body()?.string())
                            val rate:Double = ((jsonObject.getJSONArray(queryPair).getDouble(0)*1000).roundToInt().toDouble())/1000
                            (mContext as Activity).runOnUiThread{
                                exchange_country_name.text=countryName
                                Glide.with(mContext as Activity).load(resources.getIdentifier("ic_flag_"+countryWord,"drawable", (mContext as Activity).packageName)).apply(RequestOptions.circleCropTransform()).into(exchange_country_flag)
                                exchange_country_cost_info.text = "1KRW = $rate"+countryWord.toUpperCase()
                                exchange_country_cost.text =  (100/jsonObject.getJSONArray(queryPair).getDouble(0)).roundToInt().toString()+"KRW"
                            }
                        }

                        override fun onFailure(call: Call?, e: IOException?) {
                        }
                    })
                }
            })
            builder.setTitle("나라를 선택하세요")
            builder.setNegativeButton("닫기",null)

            builder.show()
        }

        goto_login.setOnClickListener {
            val intent: Intent = Intent(mContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun getLocation(){
        if(ContextCompat.checkSelfPermission(mContext as Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1000) // 권한 요청
        }else{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext as Activity)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    Log.i("위도", location?.latitude.toString())
                    Log.i("경도", location?.longitude.toString())
                    val client = OkHttpClient()

                    val apiUrl: String = "https://api.openweathermap.org/data/2.5/onecall?lang=kr&units=metric&appid=f3aff0322884727e09206a2c1fa06384"+"&lat="+location?.latitude.toString()+"&lon="+location?.longitude.toString()
                    val request = Request.Builder().url(apiUrl).build()

                    client.newCall(request).enqueue(object: Callback {
                        override fun onFailure(call: Call, e: IOException){

                            Log.i("result","fial")
                        }
                        @SuppressLint("SetTextI18n")
                        override fun onResponse(call: Call, response: Response) {
                            val jsonObject = JSONObject(response?.body()?.string())
                            val jsonArray:JSONArray = jsonObject.getJSONArray("daily")

                            //현재
                            val timezone:String = jsonObject.getString("timezone")
                            val todayTemp:String = jsonObject.getJSONObject("current").getString("temp").toDouble().roundToInt().toString()
                            val todayWeather:String = jsonObject.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description")

                            //내일
                            val tomorrowTempMin:String = jsonArray.getJSONObject(1).getJSONObject("temp").getString("min").toDouble().roundToInt().toString()
                            val tomorrowTempMax:String = jsonArray.getJSONObject(1).getJSONObject("temp").getString("max").toDouble().roundToInt().toString()
                           //val tomorrowWeather:String = jsonArray.getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("description")

                            //모레
                            val dayAfterTomorrowTempMin:String = jsonArray.getJSONObject(2).getJSONObject("temp").getString("min").toDouble().roundToInt().toString()
                            val dayAfterTomorrowTempMax:String = jsonArray.getJSONObject(2).getJSONObject("temp").getString("max").toDouble().roundToInt().toString()
                            //val dayAfterTomorrowWeather:String = jsonArray.getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("description")

                            (mContext as Activity).runOnUiThread{
                                today_weather_place_text?.text = timezone
                                today_weather_place_temp?.text = "$todayTemp℃"
                                today_weather_description?.text = todayWeather

                                tomorrow_weather_max?.text = "$tomorrowTempMax℃"
                                tomorrow_weather_min?.text = "$tomorrowTempMin℃"

                                day_after_tomorrow_weather_max?.text = "$dayAfterTomorrowTempMax℃"
                                day_after_tomorrow_weather_min?.text = "$dayAfterTomorrowTempMin℃"
                            }
                        }
                    })
                }
        }
    }


}
