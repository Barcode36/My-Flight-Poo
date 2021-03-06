package pucrs.myflight.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.jxmapviewer.viewer.GeoPosition;

import java.awt.Color;
import java.sql.Array;

import pucrs.myflight.modelo.*;

public class GerenciadorConsultas {
    
    private static List<MyWaypoint> lstPoints = new ArrayList<>();

    private static GerenciadorConsultas instance;
    private static GerenciadorRotas gerRotas;

    public GerenciadorConsultas() {
    }

    public static GerenciadorConsultas getInstance() {
        if (instance == null) {
            instance = new GerenciadorConsultas();
        }
        return instance;
    }

    public void consultaExemplo(GerenciadorMapa gerMapa) {
        // Lista para armazenar o resultado da consulta

        Aeroporto poa = new Aeroporto("POA", "Salgado Filho", new Geo(-29.9939, -51.1711));
        Aeroporto gru = new Aeroporto("GRU", "Guarulhos", new Geo(-23.4356, -46.4731));
        Aeroporto lis = new Aeroporto("LIS", "Lisbon", new Geo(38.772, -9.1342));
        Aeroporto mia = new Aeroporto("MIA", "Miami International", new Geo(25.7933, -80.2906));

        gerMapa.clear();
        Tracado tr = new Tracado();
        tr.setLabel("Teste");
        tr.setWidth(5);
        tr.setCor(new Color(0, 0, 0, 60));
        tr.addPonto(poa.getLocal());
        tr.addPonto(gru.getLocal());

        gerMapa.addTracado(tr);

        Tracado tr2 = new Tracado();
        tr2.setWidth(5);
        tr2.setCor(Color.BLUE);
        tr2.addPonto(mia.getLocal());
        tr2.addPonto(lis.getLocal());
        gerMapa.addTracado(tr2);

        // Adiciona os locais de cada aeroporto (sem repetir) na lista de
        // waypoints

        lstPoints.add(new MyWaypoint(Color.RED, poa.getCodigo(), poa.getLocal(), 5));
        lstPoints.add(new MyWaypoint(Color.RED, gru.getCodigo(), gru.getLocal(), 5));
        lstPoints.add(new MyWaypoint(Color.RED, lis.getCodigo(), lis.getLocal(), 5));
        lstPoints.add(new MyWaypoint(Color.RED, mia.getCodigo(), mia.getLocal(), 5));

        // Para obter um ponto clicado no mapa, usar como segue:
        // GeoPosition pos = gerenciador.getPosicao();

        // Informa o resultado para o gerenciador
        // gerMapa.setPontos(lstPoints);

        // Quando for o caso de limpar os tra??ados...
        // gerenciador.clear();

        gerMapa.getMapKit().repaint();
    }

    public void limpar(GerenciadorMapa gerMapa) {
        lstPoints.add(null);
        lstPoints.clear();
        gerMapa.setPontos(lstPoints);
        gerMapa.clear();
        gerMapa.getMapKit().repaint();
    
    }

    public void consulta1(GerenciadorMapa gerMapa, GerenciadorAeroportos gerAero) {
        gerMapa.clear();

        ArrayList<Rota> rotas = GerenciadorRotas.getInstance().listarTodas();

        for (Rota r : rotas) {
            Tracado tr2 = new Tracado();
            tr2.setWidth(5);
            tr2.setCor(new Color(0, 0, 0, 60));
            tr2.addPonto(r.getOrigem().getLocal());
            tr2.addPonto(r.getDestino().getLocal());
            gerMapa.addTracado(tr2);
        }

        for (Aeroporto a : gerAero.listarTodos()) {
            lstPoints.add(new MyWaypoint(Color.GREEN, a.getCodigo(), a.getLocal(), 5));
        }
        gerMapa.setPontos(lstPoints);

        Set<Aeroporto> setAeroporto = new HashSet<Aeroporto>();

        gerMapa.getMapKit().repaint();
    }

    public void plotarAeroPorCia(GerenciadorMapa gerMapa, ArrayList<Rota> rotasDaCia) {
        gerMapa.clear();
        lstPoints.clear();

        HashSet<Aeroporto> aeroportosCiaOpera = new HashSet<Aeroporto>();


        for (Rota r : rotasDaCia) {
            if (!aeroportosCiaOpera.contains(r.getOrigem())) {
                aeroportosCiaOpera.add(r.getOrigem());
            }
            if (!aeroportosCiaOpera.contains(r.getDestino())) {
                aeroportosCiaOpera.add(r.getDestino());
            }
            Tracado tr2 = new Tracado();
            tr2.setWidth(1);
            tr2.setCor(Color.BLUE);
            tr2.addPonto(r.getOrigem().getLocal());
            tr2.addPonto(r.getDestino().getLocal());
            gerMapa.addTracado(tr2);
        }
        for (Aeroporto a : aeroportosCiaOpera) {
            lstPoints.add(new MyWaypoint(Color.GREEN, a.getCodigo(), a.getLocal(), 5));
        }

        gerMapa.setPontos(lstPoints);
        gerMapa.getMapKit().repaint();
    }

    public void setTraffic(GerenciadorMapa gerMapa, GerenciadorAeroportos gerAero, HashMap<String, Integer> traffic) {
        gerMapa.clear();
        lstPoints.clear();


        for (String s : traffic.keySet()) {
            Aeroporto temp = gerAero.buscarCodigo(s);
            int tamPonto = traffic.get(s) / 10;
            if (tamPonto < 50) {
                lstPoints.add(new MyWaypoint(Color.GREEN, temp.getCodigo(), temp.getLocal(), tamPonto));
            } else {
                lstPoints.add(new MyWaypoint(Color.RED, temp.getCodigo(), temp.getLocal(), tamPonto));
            }
        }

        gerMapa.setPontos(lstPoints);
        gerMapa.getMapKit().repaint();
    }

    public void mostarEsseAeroporto(GerenciadorMapa gerMapa, Aeroporto esseAeroporto) {
        //List<MyWaypoint> lista = new ArrayList<MyWaypoint>();
        lstPoints.add(new MyWaypoint(Color.GREEN, esseAeroporto.getCodigo(), esseAeroporto.getLocal(), 15));
        //gerMapa.setPontos(lista);
        gerMapa.setPontos(lstPoints);
        gerMapa.getMapKit().repaint();
    }

    public Aeroporto getAirportFromCoord(GeoPosition pos) {
        double latitude = pos.getLatitude();
        double longitude = pos.getLongitude();
        Geo posEmGeo = new Geo(latitude, longitude);
        GerenciadorAeroportos gerAero = GerenciadorAeroportos.getInstance();
        Aeroporto fetched = gerAero.getAirportFromGPS(posEmGeo);
        return fetched;

    }

    public ArrayList<String> acharRotaComUmaConexao(String origemInicial, String destinoFinal) {
        GerenciadorRotas gerRotas = GerenciadorRotas.getInstance();

        HashMap<Aeroporto, Aeroporto> mapaOrigemInicial = gerRotas.pegaOrigem(origemInicial);
        HashMap<Aeroporto, Aeroporto> mapaDestinoFinal = gerRotas.pegaDestino(destinoFinal);

        ArrayList<String> listaDeConexoes = new ArrayList<>();
        mapaDestinoFinal.entrySet().forEach(destinoAtual -> {
            mapaOrigemInicial.entrySet().forEach(origemAtual -> {
                if (origemAtual.getKey().equals(destinoAtual.getKey())) {
                    listaDeConexoes
                            .add(origemInicial + " -> " + origemAtual.getKey().getCodigo() + " -> " + destinoFinal);

                }
            });
        });

        return listaDeConexoes;
    }

    public ArrayList<String> acharRotaComDuasConexoes(String origemInicial, String destinoFinal) {
        GerenciadorRotas gerRotas = GerenciadorRotas.getInstance();

        HashMap<Aeroporto, Aeroporto> mapaPoa = gerRotas.pegaOrigem(origemInicial);
        HashMap<Aeroporto, Aeroporto> mapaMia = gerRotas.pegaDestino(destinoFinal);
        // x = chaveDePoa y = chaveDeMia
        ArrayList<String> listaDeConexoes = new ArrayList<>();
        mapaMia.entrySet().forEach(chaveDeMia -> {
            mapaPoa.entrySet().forEach(chaveDePoa -> {
                Aeroporto xMia = chaveDePoa.getKey();
                if (xMia.equals(chaveDeMia.getKey())) {// se poa tem conexao com mia
                    // entao pulamos para xMia -> y -> mia
                    HashMap<Aeroporto, Aeroporto> mapaXMia = gerRotas.pegaOrigem(origemInicial);
                    mapaXMia.entrySet().forEach(chaveDoX -> {
                        mapaMia.entrySet().forEach(chaveDeMiaFinal -> {
                            Aeroporto yMia = chaveDeMiaFinal.getKey();
                            if (yMia.equals(chaveDoX.getKey())
                                    && !xMia.getCodigo().equalsIgnoreCase(yMia.getCodigo())) { 
                                listaDeConexoes.add(origemInicial + " -> " + xMia.getCodigo() + " -> "
                                        + yMia.getCodigo() + " -> " + destinoFinal);
                            }
                        });
                    });
                }
            });
        });
        // String temp = (origemInicial + " -> " + aeroportoX + " -> " + destinoFinal);
        return listaDeConexoes;
    }

    public void consulta3(String origem, String destino, GerenciadorMapa gerMapa) {
        GerenciadorRotas gerRotas = GerenciadorRotas.getInstance();
        ArrayList<String> direta = gerRotas.acharRotaDireta(origem, destino);
        ArrayList<String> umaConex = gerRotas.acharRotaComUmaConexao(origem, destino);
        ArrayList<String> duasConex = gerRotas.acharRotaComDuasConexoes(origem, destino);

        ArrayList<String> total = new ArrayList<>();

        total.addAll(direta);
        total.addAll(umaConex);
        total.addAll(duasConex);

        plotarRota(total, gerMapa, Color.BLUE);

        //* qq ?? isso----------------------------------
        ArrayList<String> selecao = new ArrayList<>(); //* ver depois se selecao esta sendo utilizado 
    
        selecao = ListaDeRotas.todasRotas(total,gerMapa);
        //*--------------------------------------------       
        
    }

    public ArrayList<String> consulta5(ArrayList<String> rotaTurista, GerenciadorMapa gerMapa) {
        GerenciadorRotas gerRotas = GerenciadorRotas.getInstance();
        GerenciadorAeroportos gerAero = GerenciadorAeroportos.getInstance();
        int numMaxDeAeroportos = -1;
        

       for (String string : rotaTurista) {
            if (string == null) {
                numMaxDeAeroportos = rotaTurista.indexOf(string);
                break;
            } else {
                numMaxDeAeroportos ++;
            }
        }

        ArrayList<String> temp = new ArrayList<>();
        

        for (int i = 0; i < numMaxDeAeroportos; i++) {
            String origemAtual = rotaTurista.get(i);
            String destinoAtual = rotaTurista.get(i+1);

            temp.addAll(gerRotas.acharDireto5(origemAtual, destinoAtual));
            temp.addAll(gerRotas.acharRotaComUmaConexao5(origemAtual, destinoAtual, rotaTurista));
            temp.addAll(gerRotas.acharRotaComDuasConexoes5(origemAtual, destinoAtual, rotaTurista));
        }

        // HORA DE VOLTAR PRA CASA E FERIAS
        String ultimoAeroporto = rotaTurista.get(rotaTurista.size()-1);
        String casa = rotaTurista.get(0);

        temp.addAll(gerRotas.acharDireto5(ultimoAeroporto, casa));
        temp.addAll(gerRotas.voltarPraCasaComUmaConexoesConsulta5(ultimoAeroporto, casa));
        temp.addAll(gerRotas.voltarPraCasaComDuasConexoesConsulta5(ultimoAeroporto, casa));
        ArrayList<String> total = new ArrayList<>();
        total.addAll(temp);

    
        ArrayList<String> organizado = organizacaoDeString(total, rotaTurista);

        //                         consulta5PlotarComJanela(organizado, gerMapa, rotaTurista); 

        return organizado;
        
    }


    private ArrayList<String> organizacaoDeString(ArrayList<String> tudoJunto, ArrayList<String> rotaTurista) {
        ArrayList<String> limpo = limpezaDeString(tudoJunto, rotaTurista);

        String aeroOrigem = rotaTurista.get(0);
        String aeroSegundo = rotaTurista.get(1);
        String aeroTerceiro = rotaTurista.get(2);
        String aeroQuarto = rotaTurista.get(3);
        String aeroQuinto = rotaTurista.get(4);


        ArrayList<String> listaOrigem = separacaoSmart(limpo, aeroOrigem);
        ArrayList<String> listaSegundo = separacaoSmart(limpo, aeroSegundo);
        ArrayList<String> listaTerceiro = separacaoSmart(limpo, aeroTerceiro);
        ArrayList<String> listaQuarto = separacaoSmart(limpo, aeroQuarto);
        ArrayList<String> listaQuinto = separacaoSmart(limpo, aeroQuinto);
             
        ArrayList<String> total = new ArrayList<>();
        total.addAll(listaOrigem);
        total.addAll(listaSegundo);
        total.addAll(listaTerceiro);
        total.addAll(listaQuarto);
        total.addAll(listaQuinto);

        // ArrayList<String> umComDois = juntarString(limpo, listaOrigem, aeroOrigem, listaSegundo, aeroSegundo);
        // ArrayList<String> doisComTres = juntarString(limpo, umComDois, aeroSegundo, listaTerceiro, aeroTerceiro);
        // ArrayList<String> tresComQuatro = juntarString(limpo, doisComTres, aeroTerceiro, listaQuarto, aeroQuarto);
        // ArrayList<String> quatroComQuinco = juntarString(limpo, tresComQuatro, aeroQuarto, listaQuinto, aeroQuinto);
    
        return total;
    }

    private ArrayList<String> separacaoSmart(ArrayList<String> limpo, String isso) {
        ArrayList<String> queComecaComIsso = new ArrayList<>();
        for (String rota : limpo) {
                if (rota.startsWith(isso)) {
                    queComecaComIsso.add(rota);
                    System.out.println(rota);
                }
            }
            //queComecaComIsso.add("_______________________________________________");
        return queComecaComIsso;
    }

    private ArrayList<String> limpezaDeString(ArrayList<String> tudoJunto, ArrayList<String> rotaTurista) {
        ArrayList<String> limpo = new ArrayList<>();
        
        for (String s : tudoJunto) {
            String[] aeros = s.split(" - .*?\\ horas.");
            for (String sAero : aeros) {
                if (sAero != (" |") || sAero != null) {
                    limpo.add(sAero);
                }
            }
        }

        return limpo;

    }

    private void consulta5PlotarComJanela(ArrayList<String> total, GerenciadorMapa gerMapa, ArrayList<String> rotaTurista) {
        // DisplayFinal.todasRotas(total, gerMapa, rotaTurista);
        plotarRota(total, gerMapa, Color.BLUE);

        ArrayList<String> selecao = new ArrayList<>();
        // ta sendo utilizado sim vs retardado
    
        //selecao = DisplayFinal.todasRotas(total,gerMapa, rotaTurista);
    }

    public void plotarRota(ArrayList<String> rotas, GerenciadorMapa gerMapa, Color cor) {
        GerenciadorAeroportos gerAero = GerenciadorAeroportos.getInstance();
        lstPoints.clear();
        for (String s : rotas) {
            lstPoints.clear();
            String[] aeros = s.split(";| -> |->| - Tempo aproximado total de v??o: .*? horas.|Codigo do aeroporto: | -  Pais: .*? .");
            int limite = aeros.length - 1;
            int ntraco = 0;
            for (String sAero : aeros) {
                if (!sAero.equalsIgnoreCase(";| -> |->| | - Tempo aproximado total de v??o: .*? horas.|Codigo do aeroporto: | -  Pais: .*? .")) {
                    Aeroporto aeroporto = gerAero.buscarCodigo(sAero);
                    lstPoints.add(new MyWaypoint(Color.GREEN, aeroporto.getCodigo(), aeroporto.getLocal(), 10));
                    gerMapa.setPontos(lstPoints);
                    gerMapa.getMapKit().repaint();
                    if (ntraco < limite) {
                        Aeroporto aeroOrigem = gerAero.buscarCodigo(aeros[ntraco]);
                        Aeroporto aeroDestino = gerAero.buscarCodigo(aeros[ntraco + 1]);
                        Tracado tr2 = new Tracado();
                        tr2.setWidth(1);
                        tr2.setCor(cor);
                        tr2.addPonto(aeroOrigem.getLocal());
                        tr2.addPonto(aeroDestino.getLocal());
                        gerMapa.addTracado(tr2);
                        ntraco += 1;
                    }
                }
            }
        }
    }

	public void plotarNoMapa(GerenciadorMapa gerenciador, GerenciadorAeroportos gerAero, HashSet<String> resultado) {
        HashSet<String> sanitizado = new HashSet<>();

        for(String s : resultado){
            String[] dados = s.split(";|:| -> ");
            for(String dado : dados){
                sanitizado.add(dado);
            }
        }
        for(String s2 : sanitizado){
            Aeroporto aero = gerAero.buscarCodigo(s2);
            mostarEsseAeroporto(gerenciador, aero);
        }
        
    }
    
    
    public ArrayList<String> getListaOrigem(ArrayList<String> limpo, ArrayList<String> rotaTurista) {
        String aeroOrigem = rotaTurista.get(0);
        ArrayList<String> listaOrigem = separacaoSmart(limpo, aeroOrigem);
        return listaOrigem;
    }

    public ArrayList<String> getListaSegundo(ArrayList<String> limpo, ArrayList<String> rotaTurista) {
        String aeroSegundo = rotaTurista.get(1);    
        ArrayList<String> listaSegundo = separacaoSmart(limpo, aeroSegundo);
        return listaSegundo;
    }

    public ArrayList<String> getListaTerceiro(ArrayList<String> limpo, ArrayList<String> rotaTurista) {
        String aeroTerceiro = rotaTurista.get(2);
        ArrayList<String> listaTerceiro = separacaoSmart(limpo, aeroTerceiro);
        return listaTerceiro;
    }

    public ArrayList<String> getListaQuarto(ArrayList<String> limpo, ArrayList<String> rotaTurista) {
        String aeroQuarto = rotaTurista.get(3);
        ArrayList<String> listaQuarto = separacaoSmart(limpo, aeroQuarto);
        return listaQuarto;
    }

    public ArrayList<String> getListaQuinto(ArrayList<String> limpo, ArrayList<String> rotaTurista) {
        String aeroQuinto = rotaTurista.get(4);
        ArrayList<String> listaQuinto = separacaoSmart(limpo, aeroQuinto);
        return listaQuinto;
    }
}
