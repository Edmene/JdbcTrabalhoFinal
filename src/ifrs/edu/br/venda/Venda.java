package ifrs.edu.br.venda;

import ifrs.edu.br.OperacoesCrud;
import ifrs.edu.br.ResultObjectTuple;
import ifrs.edu.br.negocio.Cliente;

import javax.sql.PooledConnection;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class Venda implements OperacoesCrud {
    private Cliente cliente;
    private float valorTotal;
    private Date date;
    private boolean status;
    private LinkedList<ItemVenda> listaItens = new LinkedList<>();
    private int idBanco;

    public Venda(Cliente cliente, Date date){
        this.cliente = cliente;
        this.date = date;
        this.status = true;
    }

    public void cancelarVenda(){
        this.status = false;
    }

    public boolean adicionarItem(ItemVenda item){
        try {
            if(!this.listaItens.contains(item)) {
                this.listaItens.add(item);
                float total = 0;
                for (ItemVenda listaIten : listaItens) {
                    total += listaIten.getValorUnitario();
                }
                this.valorTotal = total;
            }
            else {
                System.out.println("Opa valor ja presente.");
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean removerItem(ItemVenda item){
        try {
            this.listaItens.remove(item);
            float total = 0;
            for (ItemVenda listaIten : listaItens) {
                total += listaIten.getValorUnitario();
            }
            this.valorTotal = total;
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private void menuItens(){
        System.out.println("Selecione uma acao");
        System.out.println("0) Adicionar");
        System.out.println("1) Finalizar compra");
        System.out.print("Op: ");
    }

    @Override
    public String toString(){
        DecimalFormat df = new DecimalFormat("#.##");
        return String.format("valor total: %s | status: %b", df.format(this.valorTotal), this.status);
    }

    @Override
    public ResultObjectTuple cadastrar(PooledConnection connection) {
        Connection pgConnection = null;
        ResultSet resultSet = null;
        try {
            this.date = java.sql.Date.valueOf(LocalDate.now());
            pgConnection = connection.getConnection();
            Statement statement = pgConnection.createStatement();
            Cliente cli = new Cliente();
            ResultSet rs = cli.procuraRegistro(pgConnection);
            rs = selecionaRow(rs, cli);
            //Primeira Query responsavel pela insersao de uma pessoa
            statement.executeUpdate("INSERT INTO venda (venda_cliente, data, valor_total, status)" +
                    " VALUES ("+String.valueOf(rs.getInt("id"))+",'"+
                    this.date+"','"+String.valueOf(0)+"','true') RETURNING *;");
            resultSet = statement.getResultSet(); //Pegando o retorno da insersao para uso nos iten
            //rs.close();
            boolean continuarAdicionarItens = true;
            while (continuarAdicionarItens){
                menuItens();
                Scanner sc = new Scanner(System.in);
                if(sc.nextInt() == 1){
                    continuarAdicionarItens = false;
                }
                else {
                    ItemVenda item = new ItemVenda();
                    adicionarItem(item.retornaCadastro(connection));
                }
            }
            for (ItemVenda item : listaItens){
                statement.execute("INSERT INTO lista_venda (lista_item_id, lista_item_prod, venda_item)"+
                "VALUES ('"+item.getIdBanco()+"','"+item.getProdutoId()+"','"+this.idBanco+"')");
            }
                statement.executeUpdate("UPDATE venda SET valor_total='"+this.valorTotal+"'");
            /*
            //Segunda query responsavel pela insersao de um cliente
            statement.addBatch("INSERT INTO cliente (id, bandeiracc, numerocc)"+
                    " VALUES ('SELECT id from pessoa WHERE cpf = \'"+this.getCpf()+"\'','"
                    +this.bandeiraCC+"','"+this.numeroCC+"')");
            */
            //statement.executeBatch();
            pgConnection.commit();
        }
        catch (Exception e){
            try {
                pgConnection.rollback();
            }
            catch (Exception exception){
                System.err.println(exception);
            }
        }
        return new ResultObjectTuple(resultSet, this);
        //cliente = pesquisarCliente();
        //data = Date;
        //status = true;
        //listaItens = addListaItems(); //Laco adicionando produtos na lista e banco
        //valorTotal = total dos valores dos produtos
    }

    @Override
    public void editar(PooledConnection connection) {
        //this = pesquisa compra;
        //lista os items
        //pede para remover itens ou alterar quantidade
        //re-calcula o preco final
        //atualiza no banco

    }

    @Override
    public void deletar(PooledConnection connection) throws SQLException {
        //cancelar venda
        //atualiza no banco
    }

    @Override
    public ResultSet procuraRegistro(Connection connection) throws SQLException {
        return null;
    }

    @Override
    public ResultSet pesquisa(int tipo, String entrada, Statement stmt) throws SQLException {
        return null;
    }

    @Override
    public Integer construirMenu(ResultSet rs, Integer base) throws SQLException {
        return null;
    }

}
