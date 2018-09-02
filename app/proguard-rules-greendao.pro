-dontwarn org.greenrobot.**
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class org.greenrobot.greendao.**
-keep class **$Properties
-keep class com.nhatton.ggtalkvn.data.* {*;}