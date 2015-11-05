package org.asuki.tool.guava;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Comparable<Customer> {

    @Getter
    private Integer id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String address;

    public Customer(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isSick() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, address);
    }

    @Override
    public boolean equals(Object obj) {
        Customer that = (Customer) obj;

        return Objects.equal(this.id, that.id)
                && Objects.equal(this.name, that.name)
                && Objects.equal(this.address, that.address);
    }

    @Override
    public String toString() {
        // @formatter:off
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("id", id)
                .toString();
        // @formatter:on
    }

    @Override
    public int compareTo(Customer that) {
        // @formatter:off
        return ComparisonChain.start()
                .compare(this.id, that.id)
                .compare(this.name, that.name)
                .compare(this.address, that.address)
                .result();
        // @formatter:on
    }

}
