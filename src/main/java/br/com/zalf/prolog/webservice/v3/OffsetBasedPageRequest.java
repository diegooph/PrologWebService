package br.com.zalf.prolog.webservice.v3;

import lombok.Value;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Value(staticConstructor = "of")
public class OffsetBasedPageRequest implements Pageable, Serializable {
    int limit;
    int offset;
    @NotNull
    Sort sort;

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @NotNull
    @Override
    public Sort getSort() {
        return sort;
    }

    @NotNull
    @Override
    public Pageable next() {
        return OffsetBasedPageRequest.of((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    @NotNull
    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @NotNull
    @Override
    public Pageable first() {
        return OffsetBasedPageRequest.of(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(limit)
                .append(offset)
                .append(sort)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OffsetBasedPageRequest)) {
            return false;
        }

        final OffsetBasedPageRequest that = (OffsetBasedPageRequest) o;

        return new EqualsBuilder()
                .append(limit, that.limit)
                .append(offset, that.offset)
                .append(sort, that.sort)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("limit", limit)
                .append("offset", offset)
                .append("sort", sort)
                .toString();
    }

    @NotNull
    public OffsetBasedPageRequest previous() {
        return hasPrevious()
                ? OffsetBasedPageRequest.of((int) (getOffset() - getPageSize()), getPageSize(), getSort())
                : this;
    }
}
