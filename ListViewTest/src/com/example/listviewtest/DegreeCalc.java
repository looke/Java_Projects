package com.example.listviewtest;

import java.lang.Math;
public class DegreeCalc
{
	static public double calcDegree(int in)
	{
		double degree = in;
		degree = (degree-1024)/819;
		//degree = Math.toRadians(degree);
		degree = Math.asin(degree);
		degree = degree*180/Math.PI;
		return degree;
	}
	
	static public double calcPercentage(int in)
	{
		double degree = in;
		double percentage;
		degree = (degree-1024)/819;
		//degree = Math.toRadians(degree);
		degree = Math.asin(degree);
		percentage = Math.tan(degree);
		return percentage*100;
	}

}
