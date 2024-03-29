precision mediump float;

varying mediump vec2 aCoord;

uniform sampler2D vTexture;

uniform int width;
uniform int height;

vec2 blurCoordinates[20];

void main(){

    //https://blog.csdn.net/wangchao1412/article/details/103835643
    //对于100*100 的像素，1/100 步长为
    vec2 singleStepOffset = vec2(1.0/float(width), 1.0/float(height));

    blurCoordinates[0] = aCoord.xy + singleStepOffset * vec2(0.0, -10.0);
    blurCoordinates[1] = aCoord.xy + singleStepOffset * vec2(0.0, 10.0);
    blurCoordinates[2] = aCoord.xy + singleStepOffset * vec2(-10.0, 0.0);
    blurCoordinates[3] = aCoord.xy + singleStepOffset * vec2(10.0, 0.0);
    blurCoordinates[4] = aCoord.xy + singleStepOffset * vec2(5.0, -8.0);
    blurCoordinates[5] = aCoord.xy + singleStepOffset * vec2(5.0, 8.0);
    blurCoordinates[6] = aCoord.xy + singleStepOffset * vec2(-5.0, 8.0);
    blurCoordinates[7] = aCoord.xy + singleStepOffset * vec2(-5.0, -8.0);
    blurCoordinates[8] = aCoord.xy + singleStepOffset * vec2(8.0, -5.0);
    blurCoordinates[9] = aCoord.xy + singleStepOffset * vec2(8.0, 5.0);
    blurCoordinates[10] = aCoord.xy + singleStepOffset * vec2(-8.0, 5.0);
    blurCoordinates[11] = aCoord.xy + singleStepOffset * vec2(-8.0, -5.0);
    blurCoordinates[12] = aCoord.xy + singleStepOffset * vec2(0.0, -6.0);
    blurCoordinates[13] = aCoord.xy + singleStepOffset * vec2(0.0, 6.0);
    blurCoordinates[14] = aCoord.xy + singleStepOffset * vec2(6.0, 0.0);
    blurCoordinates[15] = aCoord.xy + singleStepOffset * vec2(-6.0, 0.0);
    blurCoordinates[16] = aCoord.xy + singleStepOffset * vec2(-4.0, -4.0);
    blurCoordinates[17] = aCoord.xy + singleStepOffset * vec2(-4.0, 4.0);
    blurCoordinates[18] = aCoord.xy + singleStepOffset * vec2(4.0, -4.0);
    blurCoordinates[19] = aCoord.xy + singleStepOffset * vec2(4.0, 4.0);
    //计算平均值

    vec4 currentColor = texture2D(vTexture, aCoord);
    vec3 rgb = currentColor.rgb;
    for (int i=0;i<20; i++){
        rgb += texture2D(vTexture, blurCoordinates[i].xy).rgb;
    }
    //模糊像素  //直接显示就是高斯模糊效果
    vec4 blur = vec4(rgb * 1.0/21.0, currentColor.a);

    //高反差 边缘（轮廓）高亮
    vec4 highPassColor = currentColor - blur;

    //highPassColor 强光处理 color = 2 * color1 * color2
    //24.0 强光成度
    //highPassColor 高反差基础上，边缘信息进行彩色 深度高亮效果
    highPassColor.r = clamp(2.0 * highPassColor.r * highPassColor.r * 24.0, 0.0, 1.0);
    highPassColor.g = clamp(2.0 * highPassColor.g * highPassColor.g * 24.0, 0.0, 1.0);
    highPassColor.b = clamp(2.0 * highPassColor.b * highPassColor.b * 24.0, 0.0, 1.0);
    // 过滤疤痕
    vec4 highPassBlur = vec4(highPassColor.rgb, 1.0);


    //3、融合 -> 磨皮
    //蓝色通道值
    float b = min(currentColor.b, blur.b);
    float value = clamp((b - 0.2) * 5.0, 0.0, 1.0);
    // RGB的最大值
    float maxChannelColor = max(max(highPassBlur.r, highPassBlur.g), highPassBlur.b);
    // 磨皮程度
    float intensity = 1.0;// 0.0 - 1.0f 再大会很模糊
    float currentIntensity = (1.0 - maxChannelColor / (maxChannelColor + 0.2)) * value * intensity;
    //gl_FragColor = highPassBlur;
    // 一个滤镜
    //opengl 内置函数 线性融合
    //混合 x*(1−a)+y⋅a
    // 第三个值越大，在这里融合的图像 越模糊
    vec3 r = mix(currentColor.rgb, blur.rgb, currentIntensity);
    //
    gl_FragColor = vec4(r, 1.0);

}