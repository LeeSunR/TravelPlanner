package com.leesunr.travelplanner

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class home : Fragment() {

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
            val builder = AlertDialog.Builder(ContextThemeWrapper(mContext, R.style.Theme_AppCompat_Light_Dialog))
            builder.setItems(R.array.country, object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, pos: Int) {
                    val country_word = getResources().getStringArray(R.array.country_word)[pos]
                    val country_name = getResources().getStringArray(R.array.country)[pos]
                    (mContext as Activity).runOnUiThread{
                        exchange_country_name.text=country_name
                        val id = resources.getIdentifier("ic_flag_"+country_word,"drawable", (mContext as Activity).packageName)

                        Glide.with(mContext as Activity).load(id).apply(RequestOptions.circleCropTransform()).into(exchange_country_flag)
                    }
                }
            })
            builder.setTitle("나라를 선택하세요")
            builder.setNegativeButton("닫기",null)

            builder.show()
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
                        override fun onResponse(call: Call, response: Response) {
                            val jsonObject = JSONObject(response?.body().string())
                            val jsonArray:JSONArray = jsonObject.getJSONArray("daily")

                            //현재
                            val timezone:String = jsonObject.getString("timezone")
                            val todayTemp:String = Math.round(jsonObject.getJSONObject("current").getString("temp").toDouble()).toString()
                            val todayWeather:String = jsonObject.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description")

                            //내일
                            val tomorrowTempMin:String = Math.round(jsonArray.getJSONObject(1).getJSONObject("temp").getString("min").toDouble()).toString()
                            val tomorrowTempMax:String = Math.round(jsonArray.getJSONObject(1).getJSONObject("temp").getString("max").toDouble()).toString()
                           //val tomorrowWeather:String = jsonArray.getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("description")

                            //모레
                            val dayAfterTomorrowTempMin:String = Math.round(jsonArray.getJSONObject(2).getJSONObject("temp").getString("min").toDouble()).toString()
                            val dayAfterTomorrowTempMax:String = Math.round(jsonArray.getJSONObject(2).getJSONObject("temp").getString("max").toDouble()).toString()
                            //val dayAfterTomorrowWeather:String = jsonArray.getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("description")

                            (mContext as Activity).runOnUiThread{
                                today_weather_place_text.text = timezone
                                today_weather_place_temp.text = todayTemp+"℃"
                                today_weather_description.text = todayWeather

                                tomorrow_weather_max.text = tomorrowTempMax+"℃"
                                tomorrow_weather_min.text = tomorrowTempMin+"℃"

                                day_after_tomorrow_weather_max.text = dayAfterTomorrowTempMax+"℃"
                                day_after_tomorrow_weather_min.text = dayAfterTomorrowTempMin+"℃"
                            }
                        }
                    })
                }
        }
    }


}
