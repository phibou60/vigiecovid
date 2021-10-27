package chamette.datasets.predef;

import java.util.ArrayList;
import java.util.HashMap;

import chamette.datasets.CommonDataset;

public class DepartementsDataset extends CommonDataset { 

	public DepartementsDataset() throws Exception {
		super("departements");
		load();
	}
	
	public boolean load() throws Exception {
		
		//CODDEP;DEP;NBARR;NBCAN;NBCOM;PMUN;PTOT;
		// NBARR = Nb d'arrondissements
		// NBCAN = Nb de cantons
		// NBCAN = Nb de commune
		// PMUN = ???
		// PTOT = Population totale
		ArrayList<String> datasets = new ArrayList<String>();
		datasets.add("01;Ain;4;23;393;643350;659180;");
		datasets.add("02;Aisne;5;21;800;534490;546527;");
		datasets.add("03;Allier;3;19;317;337988;347035;");
		datasets.add("04;Alpes-de-Haute-Provence;4;15;198;163915;168381;");
		datasets.add("05;Hautes-Alpes;2;15;162;141284;145883;");
		datasets.add("06;Alpes-Maritimes;2;27;163;1083310;1097496;");
		datasets.add("07;Ardèche;3;17;335;325712;334688;");
		datasets.add("08;Ardennes;4;19;449;273579;280032;");
		datasets.add("09;Ariège;3;13;327;153153;157210;");
		datasets.add("10;Aube;3;17;431;310020;317118;");
		datasets.add("11;Aude;3;19;433;370260;379094;");
		datasets.add("12;Aveyron;3;23;285;279206;289488;");
		datasets.add("13;Bouches-du-Rhône;4;29;119;2024162;2048504;");
		datasets.add("14;Calvados;4;25;527;694002;708344;");
		datasets.add("15;Cantal;3;15;246;145143;150185;");
		datasets.add("16;Charente;3;19;366;352335;361539;");
		datasets.add("17;Charente-Maritime;5;27;463;644303;659968;");
		datasets.add("18;Cher;3;19;287;304256;311456;");
		datasets.add("19;Corrèze;3;19;280;241464;249135;");
		datasets.add("2A;Corse-du-Sud;2;11;124;157249;159768;");
		datasets.add("2B;Haute-Corse;3;15;236;177689;180465;");
		datasets.add("21;Côte-d'Or;3;23;700;533819;545798;");
		datasets.add("22;Côtes-d'Armor;4;27;348;598814;617107;");
		datasets.add("23;Creuse;2;15;256;118638;122133;");
		datasets.add("24;Dordogne;4;25;505;413606;424095;");
		datasets.add("25;Doubs;3;19;573;539067;552643;");
		datasets.add("26;Drôme;3;19;364;511553;524574;");
		datasets.add("27;Eure;3;23;585;601843;614926;");
		datasets.add("28;Eure-et-Loir;4;15;365;433233;443538;");
		datasets.add("29;Finistère;4;27;277;909028;933992;");
		datasets.add("30;Gard;3;23;351;744178;757764;");
		datasets.add("31;Haute-Garonne;3;27;586;1362672;1385122;");
		datasets.add("32;Gers;3;17;461;191091;197953;");
		datasets.add("33;Gironde;6;33;535;1583384;1607545;");
		datasets.add("34;Hérault;3;25;342;1144892;1162867;");
		datasets.add("35;Ille-et-Vilaine;4;27;333;1060199;1084554;");
		datasets.add("36;Indre;4;13;241;222232;227999;");
		datasets.add("37;Indre-et-Loire;3;19;272;606511;618820;");
		datasets.add("38;Isère;3;29;512;1258722;1283384;");
		datasets.add("39;Jura;3;17;494;260188;269344;");
		datasets.add("40;Landes;2;15;327;407444;419709;");
		datasets.add("41;Loir-et-Cher;3;15;267;331915;340499;");
		datasets.add("42;Loire;3;21;323;762941;777328;");
		datasets.add("43;Haute-Loire;3;19;257;227283;234190;");
		datasets.add("44;Loire-Atlantique;3;31;207;1394909;1423152;");
		datasets.add("45;Loiret;3;21;326;678105;692540;");
		datasets.add("46;Lot;3;17;313;173828;179556;");
		datasets.add("47;Lot-et-Garonne;4;21;319;332842;341270;");
		datasets.add("48;Lozère;2;13;152;76601;80240;");
		datasets.add("49;Maine-et-Loire;4;21;177;813493;833154;");
		datasets.add("50;Manche;4;27;446;496883;512923;");
		datasets.add("51;Marne;4;23;613;568895;580671;");
		datasets.add("52;Haute-Marne;3;17;426;175640;180753;");
		datasets.add("53;Mayenne;3;17;242;307445;316750;");
		datasets.add("54;Meurthe-et-Moselle;4;23;591;733481;745300;");
		datasets.add("55;Meuse;3;17;499;187187;192588;");
		datasets.add("56;Morbihan;3;21;250;750863;771911;");
		datasets.add("57;Moselle;5;27;725;1043522;1062217;");
		datasets.add("58;Nièvre;4;17;309;207182;212742;");
		datasets.add("59;Nord;6;41;648;2604361;2635255;");
		datasets.add("60;Oise;4;21;679;824503;841948;");
		datasets.add("61;Orne;3;21;385;283372;291557;");
		datasets.add("62;Pas-de-Calais;7;39;890;1468018;1489983;");
		datasets.add("63;Puy-de-Dôme;5;31;464;653742;668301;");
		datasets.add("64;Pyrénées-Atlantiques;3;27;546;677309;695965;");
		datasets.add("65;Hautes-Pyrénées;3;17;469;228530;234591;");
		datasets.add("66;Pyrénées-Orientales;3;17;226;474452;482368;");
		datasets.add("67;Bas-Rhin;5;23;514;1125559;1141511;");
		datasets.add("68;Haut-Rhin;4;17;366;764030;777917;");
		datasets.add("69;Rhône;2;13;267;1843319;1869599;");
		datasets.add("70;Haute-Saône;2;17;539;236659;243264;");
		datasets.add("71;Saône-et-Loire;5;29;565;553595;569531;");
		datasets.add("72;Sarthe;3;21;354;566506;579650;");
		datasets.add("73;Savoie;3;19;273;431174;443787;");
		datasets.add("74;Haute-Savoie;4;17;279;807360;828417;");
		datasets.add("75;Paris;1;;1;2187526;2204773;");
		datasets.add("76;Seine-Maritime;3;35;708;1254378;1275559;");
		datasets.add("77;Seine-et-Marne;5;23;507;1403997;1420469;");
		datasets.add("78;Yvelines;4;21;259;1438266;1463091;");
		datasets.add("79;Deux-Sèvres;3;17;256;374351;384479;");
		datasets.add("80;Somme;4;23;772;572443;582464;");
		datasets.add("81;Tarn;2;23;314;387890;398412;");
		datasets.add("82;Tarn-et-Garonne;2;15;195;258349;264130;");
		datasets.add("83;Var;3;23;153;1058740;1075653;");
		datasets.add("84;Vaucluse;3;17;151;559479;570762;");
		datasets.add("85;Vendée;3;17;258;675247;693455;");
		datasets.add("86;Vienne;3;19;266;436876;447150;");
		datasets.add("87;Haute-Vienne;3;21;195;374426;381379;");
		datasets.add("88;Vosges;3;17;507;367673;378986;");
		datasets.add("89;Yonne;3;21;423;338291;346902;");
		datasets.add("90;Territoire de Belfort;1;9;101;142622;145640;");
		datasets.add("91;Essonne;3;21;194;1296130;1310599;");
		datasets.add("92;Hauts-de-Seine;3;23;36;1609306;1625917;");
		datasets.add("93;Seine-Saint-Denis;3;21;40;1623111;1630133;");
		datasets.add("94;Val-de-Marne;3;25;47;1387926;1397035;");
		datasets.add("95;Val-d'Oise;3;21;184;1228618;1239262;");
		datasets.add("971;Guadeloupe;2;21;32;390253;396153;");
		datasets.add("972;Martinique;4;;34;372594;377711;");
		datasets.add("973;Guyane;2;;22;268700;271124;");
		datasets.add("974;La Réunion;4;25;24;853659;863063;");
		datasets.add("976;Mayotte;1;13;17;256518;256518;");

		HashMap<String, HashMap> departements = new HashMap<String, HashMap>();
		for (String line: datasets) {
			HashMap<String, Object> departement = new HashMap<String, Object>();
			String[] splits = line.split(";");
			
			departement.put("DEP",   splits[1]);
			departement.put("NBARR", new Integer("0"+splits[2]));
			departement.put("NBCAN", new Integer("0"+splits[3]));
			departement.put("NBCOM", new Integer("0"+splits[4]));
			departement.put("PMUN",  new Long("0"+splits[5]));
			departement.put("PTOT",  new Long("00"+splits[6]));
			departements.put(splits[0], departement);
		}
		
		setData(departements);
		
		return true;
		
	}

}