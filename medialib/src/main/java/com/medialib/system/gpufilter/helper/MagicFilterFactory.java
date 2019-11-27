package com.medialib.system.gpufilter.helper;


import com.medialib.system.gpufilter.basefilter.GPUImageFilter;
import com.medialib.system.gpufilter.filter.MagicAntiqueFilter;
import com.medialib.system.gpufilter.filter.MagicBrannanFilter;
import com.medialib.system.gpufilter.filter.MagicCoolFilter;
import com.medialib.system.gpufilter.filter.MagicFreudFilter;
import com.medialib.system.gpufilter.filter.MagicHefeFilter;
import com.medialib.system.gpufilter.filter.MagicHudsonFilter;
import com.medialib.system.gpufilter.filter.MagicInkwellFilter;
import com.medialib.system.gpufilter.filter.MagicN1977Filter;
import com.medialib.system.gpufilter.filter.MagicNashvilleFilter;

public class MagicFilterFactory {

    private static MagicFilterType filterType = MagicFilterType.NONE;

    public static GPUImageFilter initFilters(MagicFilterType type) {
        if (type == null) {
            return null;
        }
        filterType = type;
        switch (type) {
            case ANTIQUE:
                return new MagicAntiqueFilter();
            case BRANNAN:
                return new MagicBrannanFilter();
            case FREUD:
                return new MagicFreudFilter();
            case HEFE:
                return new MagicHefeFilter();
            case HUDSON:
                return new MagicHudsonFilter();
            case INKWELL:
                return new MagicInkwellFilter();
            case N1977:
                return new MagicN1977Filter();
            case NASHVILLE:
                return new MagicNashvilleFilter();
            case COOL:
                return new MagicCoolFilter();
            case WARM:
                return new MagicWarmFilter();
            default:
                return null;
        }
    }

    public MagicFilterType getCurrentFilterType() {
        return filterType;
    }

    private static class MagicWarmFilter extends GPUImageFilter {
    }
}
