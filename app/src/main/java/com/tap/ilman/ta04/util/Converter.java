package com.tap.ilman.ta04.util;

public class Converter {

	public static Kategori toKategori(String t) {
		try {
			return Converter.determineCategory(Satuan.valueOf(t.toLowerCase()));
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();

			return Kategori.unknown;
		}
	}

	static boolean isItSimplePostType(String t) {

		try {
			Kategori k = Converter.determineCategory(Satuan.valueOf(t
					.toLowerCase()));
			if (k == Kategori.berat || k == Kategori.panjang
					|| k == Kategori.luas || k == Kategori.volume)
				return true;
			else
				return false;
		} catch (IllegalArgumentException e) {

			return false;

		}
	}

	static Kategori determineCategory(Satuan t) {

		switch (t) {
		case mm:
		case cm:
		case dm:
		case m:
		case dam:
		case hm:
		case km:
		case mil:
		case feet:
		case inci:
			return Kategori.panjang;
		case mm2:
		case cm2:
		case dm2:
		case m2:
		case dam2:
		case hm2:
		case km2:
			return Kategori.luas;
		case mg:
		case cg:
		case dg:
		case g:
		case dag:
		case hg:
		case kg:
		case ons:
		case pon:
		case ton:
		case kuintal:
			return Kategori.berat;
		case ml:
		case cl:
		case dl:
		case l:
		case dal:
		case hl:
		case kl:
		case mm3:
		case cm3:
		case dm3:
		case m3:
		case dam3:
		case hm3:
		case km3:
			return Kategori.volume;
		case jam:
		case menit:
		case detik:
			return Kategori.jmd;
		case rp:
			return Kategori.uang;
		default:
			return Kategori.unknown;
		}
	}
	
	public static float converse(Kategori k, Satuan m, float val){
		switch (k){
		case waktu: 
			return Converter.convertToSeconds(m, val);
		case panjang:
			return Converter.convertToMLength(m, val);
		case luas:
			return Converter.convertToM2Width(m, val);
		case berat:
			return Converter.convertToOnsWeight(m, val);
		case volume:
			return Converter.convertToLitreVolume(m, val);
		case jmd:
			return Converter.convertToSeconds(m, val);
		case buahan:
			return Converter.convertToBuah(m, val);		
		default: 
			System.out.println("kategori "+k+" tidak diketahui atau tidak dapat dikonversi ke satuan lainnya");
			return val;
		}		
	}
	
	public static float convertToBuah(Satuan m, float val) {
		switch (m) {
		case lusin:
			return val * 12f;
		case buah:
			return val;
		case gross:
			return val * 144f;
		case kodi:
			return val * 20f;
		default:
			System.out.println("this "+m+" metric cannot be converted in this category");
			return val;
		}
	}

	public static float convertToM2Width(Satuan m, float val) {
		switch (m) {
		case mm2:
			return val * 0.00_0001f;
		case cm2:
			return val * 0.0001f;
		case dm2:
			return val * 0.01f;
		case m2:
			return val;
		case dam2:
			return val * 100f;
		case hm2:
			return val * 10_000f;
		case km2:
			return val * 1_000_000f;
		default:
			System.out.println("this "+m+" metric cannot be converted in this category");
			return val;
		}
	}

	public static float convertToOnsWeight(Satuan m, float val) {
		switch (m) {
		case mg:
			return val * 0.0_0001f;
		case cg:
			return val * 0.0001f;
		case dg:
			return val * 0.001f;
		case g:
			return val * 0.01f;
		case dag:
			return val * 0.1f;
		case hg:
			return val;
		case ons:
			return val;
		case kg:
			return val * 10f;
		case pon:
			return val * 5f;
		case kuintal:
			return val * 1_000f;
		case ton:
			return val * 10_000f;

		default:
			System.out.println("this "+m+" metric cannot be converted in this category");
			return val;
		}
	}

	public static float convertToFeetLength(Satuan m, float val) {
		switch (m) {
		case mm:
			return val * 0.0001f;
		case cm:
			return val * 0.001f;
		case dm:
			return val * 0.1f;
		case m:
			return val;
		case dam:
			return val * 10;
		case hm:
			return val * 100;
		case km:
			return val * 1000;
		case mil:
			return val * 1860;
		case feet:
			return val * 0.3_048f;
		case inci:
			return val * 0.0_254f;
		default:
			System.out.println("this "+m+" metric cannot be converted in this category");
			return val;
		}
	}

	public static float convertToSeconds(Satuan m, float val) {
		switch (m) {
		case hari:
			return val * 3600f * 24f;
		case jam:
			return val * 3600f;
		case menit:
			return val * 60f;
		case detik:
			return val;
		default:
			System.out.println("this "+m+" metric cannot be converted in this category");
			return val;
		}
	}

	public static float convertToLitreVolume(Satuan m, float val) {
		switch (m) {
		case ml:
			return val * 0.001f;
		case cl:
			return val * 0.01f;
		case dl:
			return val * 0.1f;
		case l:
			return val;
		case dal:
			return val * 10f;
		case hl:
			return val * 100f;
		case kl:
			return val * 1000f;
		case mm3:
			return val * 0.00_0001f;
		case cm3:
			return val * 0.001f;
		case dm3:
			return val * 1f;
		case m3:
			return val * 1_000f;
		case dam3:
			return val * 1_000_000f;
		case hm3:
			return val * 1_000_000_000f;
		case km3:
			return val * 1_000_000_000_000f;
		default:
			System.out.println("this "+m+" metric cannot be converted in this category");
			return val;
		}
	}

	static boolean isItLengthZ(String s) {
		switch (Satuan.valueOf(s.toLowerCase())) {
		case mm:
		case cm:
		case dm:
		case m:
		case dam:
		case hm:
		case km:
			return true;
		default:
			return false;
		}
	}

	static boolean isItLengthUnit(String s) {
		switch (Satuan.valueOf(s.toLowerCase())) {
		case mm:
		case cm:
		case dm:
		case m:
		case dam:
		case hm:
		case km:
		case mil:
		case feet:
		case inci:
			return true;
		default:
			return false;
		}
	}

	static boolean isItWidthUnit(String s) {
		switch (Satuan.valueOf(s.toLowerCase())) {
		case mm2:
		case cm2:
		case dm2:
		case m2:
		case dam2:
		case hm2:
		case km2:
			return true;
		default:
			return false;
		}
	}

	private static boolean isItWeightUnit(String s) {
		switch (Satuan.valueOf(s.toLowerCase())) {
		case mg:
		case cg:
		case dg:
		case g:
		case dag:
		case hg:
		case kg:
		case ons:
		case pon:
		case ton:
		case kuintal:
			return true;
		default:
			return false;
		}
	}

	private static boolean isItVolumeUnit(String s) {
		switch (Satuan.valueOf(s.toLowerCase())) {

		case ml:
		case cl:
		case dl:
		case l:
		case dal:
		case hl:
		case kl:
		case mm3:
		case cm3:
		case dm3:
		case m3:
		case dam3:
		case hm3:
		case km3:
			return true;
		default:
			return false;
		}
	}

	public static float convertToMLength(Satuan m, float val) {
		switch (m) {
		case mm:
		case cm:
		case dm:
		case m:
		case dam:
		case hm:
		case km:
		case mil:
		case feet:
		case inci:
		default:
			return val;
		}
	}

	public static boolean isPossiblyPowered(String s) {
		try {
			Satuan st = Satuan.valueOf(s);
			switch (st) {
			case mm:
			case cm:
			case dm:
			case m:
			case dam:
			case hm:
			case km:
				return true;
			default:
				return false;
			}
		} catch (Exception e) {
			System.out.println("exception caught, probably wrong cast.. ");
			e.printStackTrace();
			return false;
		}
	}
}
