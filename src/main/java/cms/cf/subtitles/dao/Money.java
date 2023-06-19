package cms.cf.subtitles.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;

public final class Money
{
    private long     value;
    private Currency currency;

    //este deve ser privado. uso interno 
    private Money(long value, Currency currency)
    {
        this.value = value;
        this.currency = currency;
    }
    
    public Money(String value, Currency currency)
    {
        if (value != null) value = value.replace(',', '.');
        
        this.value = new BigDecimal(value).movePointRight(currency.getDefaultFractionDigits()).longValue();
        this.currency = currency;
    }
    
    public Money(String value, String currency)
    {
        this(value, Currency.getInstance("BRL"));
    }
    
/* bug : erros detectados ao se usar double para criar o BigDeciamal. Ex: new Money(new BigDecimal(19.99))  --> 19.98    
    public Money(BigDecimal value, Currency currency)
    {
        this.value = value.movePointRight(currency.getDefaultFractionDigits()).longValue();
        this.currency = currency;
    }
    
    public Money(BigDecimal value)
    {
        this(value, Currency.getInstance("BRL"));
    }
*/    
    
    public Money(String value)
    {
        this(value, Currency.getInstance("BRL"));
    }

    // Metodos de Instancia

    public BigDecimal getAmount()
    {
        BigDecimal amount = new BigDecimal(value);
        return amount.movePointLeft(currency.getDefaultFractionDigits());
    }

    public Currency getCurrency()
    {
        return currency;
    }

    /*
     * Com dinheiro s�o possiveis opera��es de soma e subtra��o (desde que na
     * mesma moeda), multiplica��o por numeros decimais e divis�o por inteiros.
     * 
     * Notar que a divis�o por numeros decimais � igual � multiplica��o pelo
     * inverso desse numero. As opera��es n�o precisam ser implementadas no
     * objeto se ele n�o for utilizado para calculo e apenas como representa��o,
     * mas s�o implementadas na maiora das vezes por serem uma facilidade
     * pr�tica. Para as opera��es de soma e subtra��o � necess�rio testar se a
     * moeda de ambas as parcelas � a mesma. Caso isso n�o seja verdade uma
     * exce��o ser� lan�ada.
     */
    public Money add(Money other) throws IllegalArgumentException
    {
        if (other == null)
        {
            return this;
        }
        
        if (!other.currency.equals(this.currency))
        {
            throw new IllegalArgumentException("operacao invalida com moedas distintas : [" + this.currency + "] e [" + other.currency + "]");
        }

        // � a mesma moeda. Money1 � imut�vel. Cria outro com novo valor
        return new Money(this.value + other.value, this.currency);

    }

    public Money minus(Money other) throws IllegalArgumentException
    {
        if (other == null)
        {
           return this;
        }
        
        if (!other.currency.equals(this.currency))
        {
            throw new IllegalArgumentException("operacao invalida com moedas distintas : [" + this.currency + "] e [" + other.currency + "]");
        }

        // � a mesma moeda. Money1 � imut�vel. Cria outro com novo valor
        return new Money(this.value - other.value, this.currency);

    }

    /*
     * A opera��o de multiplica��o � um pouco mais delicada. 
     * Porque � possivel multiplicar por uma quantidades decimal qualquer podemos criar um m�todo
     * que aceite qualquer numero,ou, que aceite apenas BigDecimal. 
     * �s vezes por compatibilidade com API legadas ou frameworks � necess�rio utilizar Double.
     */
    public Money multiply(Number factor)
    {
        BigDecimal bigFactor;
        if (factor instanceof BigDecimal)
        {
            bigFactor = (BigDecimal) factor;
        }
        else
        {
            bigFactor = new BigDecimal(factor.toString());
        }

        long result = bigFactor.multiply(new BigDecimal(this.value)).longValue();

        return new Money(result, currency);
    }

    /*
     * A opera��o de divis�o � mais complexa porque tem que seguir a regra de
     * divis�o inteira. 
     * 
     * Na realidade se trata de uma opera��o de destribui��o.
     * Felizmente a class BigInteger j� cont�m essas opera��es.
     */
    public Money[] distribute(int n)
    {
        BigInteger bigValue = BigInteger.valueOf(this.value);
        BigInteger[] result = bigValue.divideAndRemainder(BigInteger.valueOf(n));

        Money[] distribution = new Money[n];

        // todos os valores s�o iguais
        Arrays.fill(distribution, new Money(result[0].longValue(), this.currency));

        // adiciona o resto no primeiro
        // substituindo o valor atual nessa posi��o
        distribution[0] = distribution[0].add(new Money(result[1].longValue(), this.currency));

        return distribution;
    }
    
    public int compareTo(Money other)
    {
        if (!other.currency.equals(this.currency))
        {
            throw new IllegalArgumentException("operacao invalida com moedas distintas : [" + this.currency + "] e [" + other.currency + "]");
        }
        
        return (int) (value - other.value);
    }
    
    /**
     * Devis�o com resto, resulta num array de 2 posi��es onde o valor �
     * retornado no item 0 e o resto no item 1
     */
    public Money[] distributeEqually(int n)
    {
        BigInteger bigValue = BigInteger.valueOf(this.value);
        BigInteger[] result = bigValue.divideAndRemainder(BigInteger.valueOf(n));

        Money[] distribution = new Money[n];

        distribution[0] = new Money(result[0].longValue(), this.currency);
        distribution[1] = new Money(result[1].longValue(), this.currency);

        return distribution;
    }

    
    @Override
    public String toString()
    {
        return getAmount().toString();
    }
    
    public String toStringFormated()
    {
        double valor = getAmount().doubleValue();
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(valor);
    }    
    
    public double doubleValue()
    {
        return getAmount().doubleValue();
    }
    
    public BigDecimal bigDecimal()
    {
        return getAmount();
    }
    
    public static void main(String[] args)
    {
        Money m1 = new Money("100");
        Money[] list = m1.distribute(3);
        for (int i = 0; i < list.length; i++)
        {
            System.out.println(list[i]);
        }
        
        Money m2 = new Money("5.05", "BRL");//"USD");
        
        System.out.println(m2.add(m1));
        System.out.println(m2.multiply(30));
        System.out.println(m1.minus(m1));
        System.out.println(m1.minus(m2));
        System.out.println(m2.minus(m1));
    }
}
