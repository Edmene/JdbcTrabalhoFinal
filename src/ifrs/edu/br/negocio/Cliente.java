package ifrs.edu.br.negocio;

import ifrs.edu.br.OperacoesCrud;

import java.util.Scanner;

public class Cliente extends Pessoa implements OperacoesCrud {
    private String bandeiraCC;
    private String numeroCC;

    public Cliente(String cpf){
        this.setCpf(cpf.toCharArray());
    }

    public Cliente(){}

    public String getBandeiraCC() {
        return bandeiraCC;
    }

    public void setBandeiraCC(String bandeiraCC) {
        this.bandeiraCC = bandeiraCC;
    }

    public String getNumeroCC() {
        return numeroCC;
    }

    public void setNumeroCC(String numeroCC) {
        this.numeroCC = numeroCC;
    }

    @Override
    protected void entradaUsuario(boolean todasAsEntradas){
        super.entradaUsuario(todasAsEntradas);
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite a bandeira CC: ");
        if(!todasAsEntradas){
            String tmp = sc.nextLine();
            if (tmp.length() != 0){
                this.bandeiraCC = tmp;
            }
        }
        else {
            this.bandeiraCC = sc.nextLine();
        }
        System.out.print("Digite o numero CC: ");
        if(!todasAsEntradas){
            String tmp = sc.nextLine();
            if (tmp.length() != 0){
                this.numeroCC = tmp;
            }
        }
        else {
            this.numeroCC = sc.nextLine();
        }
    }

    @Override
    public void cadastrar() {
        entradaUsuario(true);
    }

    @Override
    public void editar() {
        entradaUsuario(false);
    }

    @Override
    public void deletar() {

    }
}
