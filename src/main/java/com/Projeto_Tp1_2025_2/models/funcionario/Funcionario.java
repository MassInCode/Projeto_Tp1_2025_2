    package com.Projeto_Tp1_2025_2.models.funcionario;

    import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
    import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
    import com.Projeto_Tp1_2025_2.models.Usuario;
    import com.fasterxml.jackson.annotation.JsonFormat;

    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.time.format.DateTimeFormatter;

    public class Funcionario extends Usuario{
        private double salariobruto;
        private double salarioliquido;
        private boolean status;

        // essa annotation serve para o localdate ser apropriadamente alocado na database
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private LocalDate dataContratacao;

        private Regime regime;
        private String departamento;
        private String cargoContratado;

        public Funcionario(String nome, String senha, String cpf, String email, String cargo,double salario,boolean status,String dataContratacao,String regime, String departamento) {
            super(nome, senha, cpf, email, cargo);
            this.salariobruto = salario;
            this.status = status;
            this.dataContratacao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
            this.regime = descobrirRegime(regime);
            this.departamento = departamento;
            this.cargoContratado = cargo;
        }

        //nao apagar
        public Funcionario(){super();}
        //nao apagar


        public Funcionario(int id, String nome, String senha, String cpf, String email, String cargo,double salario,boolean status,String dataContratacao,String regime, String departamento) {
            super(id, nome, senha, cpf, email, cargo);
            this.salariobruto = salario;
            this.status = status;
            this.dataContratacao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
            this.regime = descobrirRegime(regime);
            this.departamento = departamento;
            this.cargoContratado = cargo;
        }

        public Funcionario(String nome, String senha, String cpf, String email, String cargo) {
            super(nome, senha, cpf, email, cargo);
            this.salariobruto = 0.0;
            this.status = false;
            this.dataContratacao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
            this.regime = Regime.FORADEREGIME;
            this.departamento = "NULL";
            this.cargoContratado = cargo;
        }

        public Funcionario(Usuario user) {
            super(user.getId(), user.getNome(), user.getSenha(), user.getCpf(), user.getEmail(), user.getCargo());
            this.salariobruto = 0.0;
            this.status = false;
            this.dataContratacao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
            this.regime = Regime.FORADEREGIME;
            this.departamento = "NULL";
            this.cargoContratado = user.getCargo();
        }

        //para contratação ^^
        public Funcionario(int id, Usuario usuario, double salariobruto, String regime, String departamento, String cargoContratado) throws InvalidPassword, InvalidCPF {
            //A SENHA PADRÃO VAI SER O 4 PRIMEIROS DIGITOS DO NOME + 4 PRIMEIROS DIGITOS DO CPF
            super(id, usuario.getNome(), usuario.getNome().toUpperCase().substring(0, 4) + usuario.getCpf().substring(0, 4), usuario.getCpf(), usuario.getEmail(), "FUNCIONARIO");
            this.salariobruto = salariobruto;
            this.status = true;
            this.dataContratacao = LocalDate.now();
            this.regime = descobrirRegime(regime);
            this.departamento = departamento;
            this.cargoContratado = cargoContratado;
        }

        public double getSalariobruto() {
            return salariobruto;
        }
        public String getDataContratacao() {
            return dataContratacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public boolean isStatus() {
            return status;
        }

        public void editarDados(String nome, String cpf, String email, String cargo, String departamento, boolean status) throws InvalidCPF {
                this.setCpf(cpf);
                this.setNome(nome);
                this.setEmail(email);
                this.setCargo(cargo);
                this.departamento = departamento;
                this.status = status;
        }
        public Regime descobrirRegime(String regime)
        {
            if (regime.equals("CLT")) return Regime.CLT;
            else if (regime.equals("PJ")) {return Regime.PJ;}
            else {return Regime.ESTAGIARIO;}
        }

        public Regime getRegime() {
            return regime;
        }

        public String getDepartamento() {
            return departamento;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public void changeStatus() {
            this.status = !this.status; // alterna de true pra false
        }

        public boolean getStatus() {
            return status;
        }
    }
