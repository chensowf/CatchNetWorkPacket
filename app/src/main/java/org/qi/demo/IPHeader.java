package org.qi.demo;

public class IPHeader {

    public static final byte TCP = 6;  //tcp协议号
    public static final byte UDP = 17; //udp协议号

    static final byte offset_ver_ihl = 0; // 0: 版本(4 bits) + 包头长度位置 (4// bits)
    static final byte offset_tos = 1; // 1: 服务类型位置
    static final short offset_tlen = 2; // 2: ip包总长度位置
    static final short offset_identification = 4; // :4 ip包id位置
    static final short offset_flags_fo = 6; // 6: 标记 (3 bits) + 片位移位置 (13 bits)
    static final byte offset_ttl = 8; // 8: 存活时间位置
    public static final byte offset_proto = 9; // 9: 上层协议位置
    static final short offset_crc = 10; // 10: Header 校验码位置
    public static final int offset_src_ip = 12; // 12: 源ip地址位置
    public static final int offset_dest_ip = 16; // 16: 目标ip地址位置
    static final int offset_op_pad = 20; // 20: 可选项和补码位置

    public byte[] m_Data;
    public int m_Offset;

    public IPHeader(byte[] data, int offset) {
        this.m_Data = data;
        this.m_Offset = offset;
    }

    public void Default() {
        setHeaderLength(20);
        setTos((byte) 0);
        setTotalLength(0);
        setIdentification(0);
        setFlagsAndOffset((short) 0);
        setTTL((byte) 64);
    }

    public int getDataLength() {
        return this.getTotalLength() - this.getHeaderLength();
    }

    public int getHeaderLength() {
        return (m_Data[m_Offset + offset_ver_ihl] & 0x0F) * 4;
    }

    public void setHeaderLength(int value) {
        m_Data[m_Offset + offset_ver_ihl] = (byte) ((4 << 4) | (value / 4));
    }

    public byte getTos() {
        return m_Data[m_Offset + offset_tos];
    }

    public void setTos(byte value) {
        m_Data[m_Offset + offset_tos] = value;
    }

    public int getTotalLength() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_tlen) & 0xFFFF;
    }

    public void setTotalLength(int value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_tlen, (short) value);
    }

    public int getIdentification() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_identification) & 0xFFFF;
    }

    public void setIdentification(int value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_identification, (short) value);
    }

    public short getFlagsAndOffset() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_flags_fo);
    }

    public void setFlagsAndOffset(short value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_flags_fo, value);
    }

    public byte getTTL() {
        return m_Data[m_Offset + offset_ttl];
    }

    public void setTTL(byte value) {
        m_Data[m_Offset + offset_ttl] = value;
    }

    public byte getProtocol() {
        return m_Data[m_Offset + offset_proto];  //偏移9个字节拿到协议类型得参数，刚好是一个byte8位
    }

    public void setProtocol(byte value) {
        m_Data[m_Offset + offset_proto] = value;
    }

    public short getCrc() {
        return CommonMethods.readShort(m_Data, m_Offset + offset_crc);
    }

    public void setCrc(short value) {
        CommonMethods.writeShort(m_Data, m_Offset + offset_crc, value);
    }

    /**
     * 源ip是在第12字节哪里，因为ip长度是32位，我们这里为了方便把它转为32得int类型
     * @return
     */
    public int getSourceIP() {
      //  return CommonMethods.readInt(m_Data, m_Offset + offset_src_ip);
        int r = ((m_Data[ m_Offset + offset_src_ip] & 0xFF) << 24)
                | ((m_Data[ m_Offset + offset_src_ip + 1] & 0xFF) << 16)
                | ((m_Data[ m_Offset + offset_src_ip + 2] & 0xFF) << 8) | (m_Data[ m_Offset + offset_src_ip + 3] & 0xFF);
        return r;
    }

    public void setSourceIP(int value) {
        CommonMethods.writeInt(m_Data, m_Offset + offset_src_ip, value);
    }

    /**
     * 目标ip是在第16字节哪里，因为ip长度是32位，我们这里为了方便把它转为32得int类型
     * @return
     */
    public int getDestinationIP() {
      //  return CommonMethods.readInt(m_Data, m_Offset + offset_dest_ip);
        int r = ((m_Data[ m_Offset + offset_dest_ip] & 0xFF) << 24)
                | ((m_Data[ m_Offset + offset_dest_ip + 1] & 0xFF) << 16)
                | ((m_Data[ m_Offset + offset_dest_ip + 2] & 0xFF) << 8) | (m_Data[ m_Offset + offset_dest_ip + 3] & 0xFF);
        return r;
    }

    public void setDestinationIP(int value) {
        CommonMethods.writeInt(m_Data, m_Offset + offset_dest_ip, value);
    }

    @Override
    public String toString() {
        return String.format("%s->%s Pro=%s,HLen=%d", CommonMethods.ipIntToString(getSourceIP()), CommonMethods.ipIntToString(getDestinationIP()), getProtocol(), getHeaderLength());
    }

}
