package com.leesunr.travelplanner.fragment

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.leesunr.travelplanner.R
import com.leesunr.travelplanner.adapter.LockPlanRcyAdapter
import com.leesunr.travelplanner.model.Plan
import com.leesunr.travelplanner.model.Weather
import com.leesunr.travelplanner.retrofit.INodeJS
import com.leesunr.travelplanner.retrofit.MyServerAPI
import com.leesunr.travelplanner.retrofit.RetrofitClientWithAccessToken
import com.leesunr.travelplanner.util.App
import kotlinx.android.synthetic.main.activity_lock_screen.*
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private var mContext: Context? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_REQUEST_CODE = 0xDA41
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        weatherViewUpdate()
        getLocation()
        groupPlnaLoad()
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
                            exchange_placeholder.visibility = INVISIBLE
                            val jsonObject = JSONObject(response?.body()?.string())
                            Log.e("result",jsonObject.toString())
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
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun getLocation(){
        if(ContextCompat.checkSelfPermission(mContext as Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE) // 권한 요청
        }else{
            weatherUpdate();
        }
    }


    fun weatherUpdate(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext as Activity)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
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
                        var weather = arrayOf(Weather(),Weather(),Weather(),Weather(),Weather());

                        weather[0].weatherIcon = jsonObject.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon")
                        weather[0].weatherName = jsonObject.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description")
                        weather[0].tempNow = jsonObject.getJSONObject("current").getString("temp").toDouble().roundToInt()
                        weather[0].timezone = jsonObject.getString("timezone")
                        weather[0].date = Date(jsonObject.getJSONObject("current").getLong("dt")*1000L)

                        for (i in 1 until 5){
                            weather[i].tempMin = jsonArray.getJSONObject(i)
                                .getJSONObject("temp")
                                .getString("min")
                                .toDouble()
                                .roundToInt()
                            weather[i].tempMax = jsonArray.getJSONObject(i)
                                .getJSONObject("temp")
                                .getString("max")
                                .toDouble()
                                .roundToInt()
                            weather[i].weatherIcon = jsonArray.getJSONObject(i)
                                .getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("icon")
                            weather[i].date = Date(jsonArray.getJSONObject(i).getLong("dt")*1000L)
                        }

                        App.prefs_weather.weatherJsonString = Gson().toJson(weather)
                        weatherViewUpdate()
                    }
                })
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==LOCATION_REQUEST_CODE &&  grantResults.size > 0){
            weatherUpdate()
        }else
            Log.e("[error]","LOCATION_REQUEST_CODE Permission denied")
    }
    
    fun weatherViewUpdate(){

        val gson = Gson()
        val jsonString = App.prefs_weather.weatherJsonString
        if(jsonString==null) return
        var weatherArray = gson.fromJson(jsonString, Array<Weather>::class.java)   
        
        (mContext as Activity).runOnUiThread{
            today_weather_place_text?.text = weatherArray[0].timezone
            today_weather_place_temp?.text = "${weatherArray[0].tempNow}℃"
            today_weather_description?.text = weatherArray[0].weatherName
            if(today_weather_image!=null)
                Glide.with(mContext as Activity).load("http://openweathermap.org/img/wn/${weatherArray[0].weatherIcon}@2x.png").into(today_weather_image)

            weather_day_1?.text = resources.getStringArray(R.array.dayOfWeek)[weatherArray[1].getDayOfWeek()]
            weather_max_1?.text = "${weatherArray[1].tempMax}℃"
            weather_min_1?.text = "${weatherArray[1].tempMin}℃"
            if(weather_image_1!=null)
                Glide.with(mContext as Activity).load("http://openweathermap.org/img/wn/${weatherArray[1].weatherIcon}@2x.png").into(weather_image_1)

            weather_day_2?.text = resources.getStringArray(R.array.dayOfWeek)[weatherArray[2].getDayOfWeek()]
            weather_max_2?.text = "${weatherArray[2].tempMax}℃"
            weather_min_2?.text = "${weatherArray[2].tempMin}℃"
            if(weather_image_2!=null)
                Glide.with(mContext as Activity).load("http://openweathermap.org/img/wn/${weatherArray[2].weatherIcon}@2x.png").into(weather_image_2)

            weather_day_3?.text = resources.getStringArray(R.array.dayOfWeek)[weatherArray[3].getDayOfWeek()]
            weather_max_3?.text = "${weatherArray[3].tempMax}℃"
            weather_min_3?.text = "${weatherArray[3].tempMin}℃"
            if(weather_image_3!=null)
                Glide.with(mContext as Activity).load("http://openweathermap.org/img/wn/${weatherArray[3].weatherIcon}@2x.png").into(weather_image_3)

            weather_day_4?.text = resources.getStringArray(R.array.dayOfWeek)[weatherArray[4].getDayOfWeek()]
            weather_max_4?.text = "${weatherArray[4].tempMax}℃"
            weather_min_4?.text = "${weatherArray[4].tempMin}℃"
            if(weather_image_4!=null)
                Glide.with(mContext as Activity).load("http://openweathermap.org/img/wn/${weatherArray [4].weatherIcon}@2x.png").into(weather_image_4)

        }
    }

    fun groupPlnaLoad(){
        var planList:ArrayList<Plan> = ArrayList<Plan>()
        var planAdapter: LockPlanRcyAdapter? = LockPlanRcyAdapter(mContext!!, planList)

        rcv_home_plan.layoutManager = LinearLayoutManager(mContext)
        rcv_home_plan.adapter = planAdapter
        val myAPI = RetrofitClientWithAccessToken.instance.create(INodeJS::class.java)
        MyServerAPI.call(mContext as Activity, myAPI.loadPlanList(App.mainGroupNumber.mainGroupNumber),
            { result ->
                val jsonArray:JSONArray = JSONArray(result)
                for (i in 0 until jsonArray.length()){
                    val plan = Plan().parsePlan(jsonArray.getJSONObject(i))
                    val now = Date()
                    now.time -= 86400000
                    if(plan.start_date!!.after(now)){
                        planList.add(plan)
                    }
                }
                planAdapter!!.notifyDataSetChanged()
                if(planList.isEmpty()){
                    tv_home_plan_info.text = "오늘 이후 일정이 없습니다"
                    tv_home_plan_info.visibility = View.VISIBLE
                }
                else
                    tv_home_plan_info.visibility = View.GONE
            },
            { error ->
                Log.e("PlanList error", error)
                return@call true
            }
        )
    }
}
